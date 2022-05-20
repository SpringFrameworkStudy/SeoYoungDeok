# 자바 기반의 컨테이너 설정
프로그램으로 빈과 스프링 컨테이너를 설정하는 접근 방법

## @Configuration과 @Bean 애너테이션으로 빈 설정하기
클래스에 @Configuration을 설정하면 클래스 안에는 @Bean을 설정한 메서드가 1개 이상 있고, 메서드는 빈 인스턴스를 생성해 반환한다.<br/>
스프링 컨테이너는 해당 메서드가 반환한 빈 인스턴스를 관리한다.

```Java
@Configuration
public class BankAppConfiguration {
    .....
	@Bean(name = "fixedDepositService")
	public FixedDepositService fixedDepositService() {
		return new FixedDepositServiceImpl();
	}
    .....
}
```
name 속성 : 빈 인스턴스를 스프링 컨테이너에 등록할 때 사용하는 빈 이름 (xml에서 bean의 id와 같음), 지정하지 않으면 메서드 이름을 빈 이름으로 간주한다.
@Bean을 설정한 메서드에 @Lazy, @DependsOn, @Primary, @Scope 애너테이션을 덧붙여 설정할 수 있다.


```Java
@Service
public class TransactionServiceImpl implements TransactionService {
	@Autowired
	private TransactionDao transactionDao;

	@Override
	public void getTransactions(String customerId) {
		transactionDao.getTransactions(customerId);
	}

	@Bean
	public TransactionDao transactionDao() {
		return new TransactionDaoImpl();
	}
}
```
@Component나 @Named를 설정한 클래스 안에서도 @Bean 메서드를 정의할 수 있다.(@Configuration을 사용하는 것을 권장, @Bean 메소드를 우연히 일반 메소드처럼 호출할 수 있다는 문제점)<br/>
TransactionServiceImpl 클래스에서 @Bean을 통해 등록된 transactionDao 메서드가 반환하는 TransactionDaoImpl 인스턴스를 transactionDao가 @Autowired로 주입받는다.<br/>
@Configuration 애너테이션에 @Component 메타 어노테이션이 붙어 있어 둘 사이에 비슷한 점이 많다.<br/>
두 클래스 모두 @Bean을 설정한 메서드를 정의하거나, 자동 연결을 사용할 수 있고, 스프링 컨테이너는 @Configuration, @Component클래스 인스턴스를 모두 빈으로 등록하는 등의 공통점이 있다.

### @ComponentScan 어노테이션으로 빈 검색하고 등록하기
xml의 \<component-scan\>과 같은 역할을 한다.<br/>
basePackages 속성으로 검색해야 하는 패키지(들)를 지정한다.
### 클래스경로 스캔 대신 컴포넌트 인덱스 사용하기
스프링 5에서 생긴 spring-context-indexer 모듈로 컴파일 시점에 스프링 컴포넌트 인덱스를 생성하는 기능을 프로젝트에 추가할 수 있다.<br/>
애플리케이션을 시작할 때는 생성된 인덱스를 사용해 스프링 컴포넌트를 로드한다.
```Xml
    <dependencies>
        .....
        <dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context-indexer</artifactId>
			<version>${spring.version}</version>
		</dependency>
	</dependencies>
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.7.0</version>
            <configuration>
                <source>9</source>
                <target>9</target>
                <encoding>UTF-8</encoding>
                <annotationProcessors>
                    <annotationProcessor>
                        org.springframework.context.index.CandidateComponentsIndexer
                    </annotationProcessor>
                </annotationProcessors>
            </configuration>
        </plugin>
    </plugins>
</build>
```
gradle (4.5 이전)
```gradle
dependencies {
    compileOnly("org.springframework:spring-context-indexer:{spring-version}")
}
```
gradle (4.6 이후)
```
dependencies {
    annotationProcessor "org.springframework:spring-context-indexer:{spring-version}"
}
```
maven으로 컴파일하면 target/classes/META-INF 폴더 안에 spring.components 파일이 생성되고 그 안에 스프링 컴포넌트의 인덱스가 들어있다.<br/>
@Indexed를 설정한 클래스를 모두 인덱스에 넣는데, spring 5에서는 @Configuration과 @Component 모두 @Indexed가 메타 에너테이션 되어있다.<br/>
이를 사용하면 @ComponentSacne 에너테이션이 무시되고 spring.components 파일에 들어 있는 컴포넌트를 로드한다. -> 대규모 응용 프로그램의 시작 성능을 향상시킬 수 있음
## 빈 의존 관계 주입하기
@Bean 메서드가 생성하는 빈의 의존 관계를 만족시키는 방법
1. 명시적으로 의존 관계를 생성해 반환하는 @Bean 메서드를 호출해서 의존 관계를 얻어온다.
2. @Bean 메서드 인수로 의존 관계를 지정한다. 스프링 컨테이너는 @Bean 메서드를 호출할 때 의존 관계에 대해 책임을 지며, 메서드 인수로 의존 관계를 공급한다.
3. 빈 클래스에서 @Autowired, @Inject, @Resource 등을 사용해 의존 관계를 자동 연결한다.

1번 방법
```Java
@Configuration
public class BankAppConfiguration {
	
	@Bean(name = "accountStatementService")
	public AccountStatementService accountStatementService() {
		AccountStatementServiceImpl accountStatementServiceImpl = new AccountStatementServiceImpl();
		accountStatementServiceImpl.setAccountStatementDao(accountStatementDao());
		return accountStatementServiceImpl;
	}

	@Bean(name = "accountStatementDao")
	public AccountStatementDao accountStatementDao() {
		return new AccountStatementDaoImpl();
	}
    .....
}
```
AccountStatementService 메서드는 accountStatementServiceImpl 빈을 생성하고, accountStatementDao 메서드는 AccountStatementDaoImpl 빈을 생성한다.<br/>
AccountStatementServiceImpl이 AccountStatementDaoImpl에 의존하기 때문에 여기서는 accountStatementDao메서드를 호출해서 AccountStatementDaoImpl 빈을 얻어서 AccountStatementServiceImpl 인스턴스에 설정한다.<br/>
@Bean 메서드의 동작이 빈 설정을 준수하기 때문에 accountStatementDao 메서드를 여러 번 호출해도 AccountStatementDaoImpl 인스턴스 빈이 여러 개 생기지는 않는다.<br/>
AccountStatementDao 빈이 싱글턴 스코프이므로 AccountStatementDao 메서드는 항상 같은 AccountStatementDaoImpl 인스턴스를 반환한다.

2번 방법
```Java
@Configuration
public class BankAppConfiguration {
	
	@Bean(name = "accountStatementService")
	public AccountStatementService accountStatementService(AccountStatementDao accountStatementDao) {
		AccountStatementServiceImpl accountStatementServiceImpl = new AccountStatementServiceImpl();
		accountStatementServiceImpl.setAccountStatementDao(accountStatementDao);
		return accountStatementServiceImpl;
	}

	@Bean(name = "accountStatementDao")
	public AccountStatementDao accountStatementDao() {
		return new AccountStatementDaoImpl();
	}
    .....
}
```
AccountStatementDao 빈이 accountStatementService 메서드의 인수로 정의되어 있다.<br/>
스프링 컨테이너는 내부에서 accountStatementDao를 호출하면서 accountStatementService 메서드로 AccountStatementDao 빈 인스턴스를 넘긴다.

3번 방법
```Java
@Configuration
public class BankAppConfiguration {
	.....
	@Bean(name = "fixedDepositService")
	public FixedDepositService fixedDepositService(FixedDepositDao fixedDepositDao) {
		return new FixedDepositServiceImpl();
	}

	@Bean
	public FixedDepositDao fixedDepositDao() {
		return new FixedDepositDaoImpl();
	}
    .....
}
```
FixedDepositServiceImpl이 FixedDepositDao에 의존하기는 하지만 FixedDepositDaoImpl을 FixedDepositServiceImpl에 설정하지는 않는다.<br/>
그 대신 FixedDepositDaoImpl에 대한 FixedDepositServiceImpl의 의존 관계를 @Autowired로 지정한다.
```Java
package sample.spring.chapter07.bankapp.service;

import org.springframework.beans.factory.annotation.Autowired;

import sample.spring.chapter07.bankapp.dao.FixedDepositDao;
import sample.spring.chapter07.bankapp.domain.FixedDepositDetails;

public class FixedDepositServiceImpl implements FixedDepositService {
	
	@Autowired
	private FixedDepositDao fixedDepositDao;
	
	@Override
	public void createFixedDeposit(FixedDepositDetails fdd) throws Exception {
		// -- create fixed deposit
		fixedDepositDao.createFixedDeposit(fdd);
	}
}
```
@Autowired 애너테이션을 통해 설정 class의 fixedDepositDao 메서드가 생성한 FixedDepositDao 빈을 자동 연결한다.<br/>

1, 2번 예시
```Java
package sample.spring.chapter07.bankapp.service;

import java.util.Date;

import sample.spring.chapter07.bankapp.dao.AccountStatementDao;
import sample.spring.chapter07.bankapp.domain.AccountStatement;

public class AccountStatementServiceImpl implements AccountStatementService {
	private AccountStatementDao accountStatementDao;

	public void setAccountStatementDao(AccountStatementDao accountStatementDao) {
		this.accountStatementDao = accountStatementDao;
	}

	@Override
	public AccountStatement getAccountStatement(Date from, Date to) {
		return accountStatementDao.getAccountStatement(from, to);
	}
}
```

Xml을 사용하는 스프링 컨테이너를 ClassPathXmlApplicationContext 클래스의 인스턴스로 만들었다.<br/>
빈의 소스로 @Configuration을 설정한 클래스를 사용하려면 AnnotationConfigApplicationContext 클래스의 인스턴스를 만들어서 스프링 컨테이너를 표현해야 한다.
```Java
public class BankApp {
	public static void main(String args[]) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BankAppConfiguration.class);
		.....
        FixedDepositService fixedDepositService = context.getBean(FixedDepositService.class);
		fixedDepositService.createFixedDeposit(new FixedDepositDetails(1, 1000, 12, "someemail@somedomain.com"));
		.....
		context.close();
	}
}
```
AnnotationConfigApplicationContext의 생성자 인수로 BankAppConfiguration 클래스를 넘겼다.<br/>
여러 @Configuration에 @Bean 메서드를 나눠 정의한다면 AnnotationConfigApplicationContext의 생성자에 모든 @Configuration 클래스를 전달한다.<br/>
@Named나 @Component 클래스도 전달할 수 있고, register 메서드를 통해 context에 클래스를 등록할 수도 있다.
context의 scan 메서드는 component-scan과 같은 역할을 한다.
## 생명주기 콜백
@Bean 메서드에 의해 생성되는 빈에 @PostConstruct, @PreDestroy 메서드가 정의되어 있으면 스프링 컨테이너가 이들을 호출한다.<br/>
@Bean 메서드에 의해 생성되는 빈이 생명주기 인터페이스(InitializingBean, DisposableBean 등)나 스프링 *Aware 인터페이스(ApplicationContextAware, BeanNameAware 등)을 구현한다면, 스프링 컨테이너는 이런 구현에 들어 있는 함수도 호출한다.<br/>
@Bean의 커스텀 초기화, 정리 메서드 지정 속성 initMethod, destroyMethod 속성이 있다.<br/>

```Java
@Configuration
public class SomeConfig {
    .....
    @Bean(initMethod = "initialize", destroyMethod = "close")
    public SomeBean someBean() {
        return new SomeBean();
    }
    .....
}
```
스프링 컨테이너가 초기화를 위해 SomeBean의 initialize를 호출하고, 정리를 위해 close를 호출한다.
```Java
@Configuration
public class SomeConfig {
    .....
    @Bean(initMethod = "initialize", destroyMethod = "close")
    public SomeBean someBean() {
        SomeBean bean = new SomeBean();
        bean.initialize();
        return bean;
    }
    .....
}
```
명시적으로 원하는 시기에 호출할 수도 있다.<br/>
destroyMethod는 기본적으로 close나 shutdown 메서드를 빈의 디폴트 정리 메서드로 간주한다.<br/>
이런 동작 방식을 오버라이드하려면 이런식으로 빈 문자열로 설정해야 한다.
```Java
@Bean(destroyMethod="")
public SomeBean someBean() {
    return new SomeBean();
}
```

## @Import
여러 @Configuration 파일을 하나로 합치려면 @Import 애너테이션을 활용한다.
```Java
@Configuration
@Import({BankDaosConfig.class, BankOtherObjects.class})
public class BankServicesConfig {
    .....
}
```
@Component로 정의한 클래스가 있으면 이런 컴포넌트 클래스를 @Configuration 클래스 안으로 임포트할 수 있다.
```Java
@Import({ TransactionServiceImpl.class, TransactionDaoImpl.class })
public class BankOtherObjects {
    .....
}
```

### 여러 @Configuration에서 의존 관계 해결하기
두가지 접근 방법
- @Bean 메서드의 인수로 빈 의존 관계를 지정한다.
- 임포트한 @Configuration 클래스를 자동 연결하고, 그 안에 @Bean 메서드를 호출해서 의존 관계를 가져온다.
```Java
@Configuration
@Import({BankDaosConfig.class, BankOtherObjects.class}) // @Configuration 클래스 Import
public class BankServicesConfig {
	@Autowired
	private BankDaosConfig bankAppDao; // BankDaosConfig 자동 연결
	
	@Bean(name = "accountStatementService")
	public AccountStatementService accountStatementService(AccountStatementDao accountStatementDao) { // @Bean 메서드의 인수로 AccountStatementDao를 가져와 빈 의존 관계를 지정
		AccountStatementServiceImpl accountStatementServiceImpl = new AccountStatementServiceImpl();
		accountStatementServiceImpl.setAccountStatementDao(accountStatementDao);
		return accountStatementServiceImpl;
	}
    .....
	@Bean(name = "fixedDepositService")
	public FixedDepositService fixedDepositService() {
		return new FixedDepositServiceImpl(bankAppDao.fixedDepositDao()); //bankAppDao의 @Bean 메서드를 호출해서 의존 관계를 가져온다.
	}
}
```
첫번째 방법의 단점 : AccountStatementDao 빈을 만들어내는 @Configuration 클래스를 쉽게 찾기 어려움

### XML 파일 임포트하기
일부 설정 정보가 XML 파일에 들어 있는 경우, @ImportResource 에너테이션을 통해 로드할 수 있다.