# 3주차. 어노테이션 기반 개발
## 어노테이션?
[자바 어노테이션(Annotation)](https://velog.io/@jkijki12/annotation) : 자바 소스 코드에 추가하여 사용할 수 있는 메타데이터의 일종이다.<br /> 보통 @ 기호를 앞에 붙여서 사용한다.<br />
JDK 1.5 버전 이상에서 사용 가능하다.<br /> 자바 어노테이션은 클래스 파일에 임베디드되어 컴파일러에 의해 생성된 후 자바 가상머신에 포함되어 작동한다.<br />

### 과거의 파일 관리 방법
자바 코드와 관련 설정 파일을 따로 저장해서 관리<br />
두 가지의 어려움 존재
1. 사람들이 자바 코드는 변경하는데 설정 파일은 업데이트 하지 않는 어려움
2. 설정과 코드가 분리되어 있어, 개발에 대한 어려움

> 자바 코드와 설정 파일을 하나로 합쳐서 관리하자

### 어노테이션의 종류
1. 표준 어노테이션 : 자바가 기본적으로 제공
2. 메타 어노테이션 : 어노테이션을 위한 어노테이션
3. 사용자정의 어노테이션 : 사용자가 직접 정의하는 어노테이션

#### 표준 어노테이션
1. @Override<br/>
오버라이딩을 올바르게 했는지 컴파일러가 체크
2. @Deperecated<br/>
앞으로 사용하지 않을 것을 권장하는 필드나 메서드에 붙인다.<br/>
해당 메서드로 개발을 진행한 프로젝트들에 대한 하위 호환성을 위해 메서드를 유지하지만, 권장하지 않는다는 것을 표시한다.
3. @FunctionalInterface<br/>
[함수형 인터페이스](https://velog.io/@jaden_94/%ED%95%A8%EC%88%98%ED%98%95-%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4-Functional-Interface)에 붙이면, 컴파일러가 올바르게 작성했는지 체크<br/>
함수형 인터페이스의 "하나의 추상메서드만 가져야 한다는 제약"을 확인해준다.<br/>
또한 함수형 인터페이스라는 것을 알려주는 역할도 한다.<br/>
[JAVA8 Lambda람다는 @FunctionalInterface의 구현체이다.](https://javaplant.tistory.com/32)<br/>
[JAVA8 람다](https://skyoo2003.github.io/post/2016/11/09/java8-lambda-expression)<br/>
4. @SuppressWarning<br/>
컴파일러의 경고메세지가 나타나지 않게 한다.
#### 메타 어노테이션
1. @Target</br>
어노테이션을 정의할 때, 적용대상을 지정하는데 사용한다.
2. @Retention<br/>
어노테이션이 유지되는 기간을 지정하는데 사용
- SOURCE : 소스 파일에만 존재
- RUNTIME : 클래스 파일에 존재, 실행시에 사용가능
3. @Documented<br/>
javadoc으로 작성한 문서에 포함시키려 할 때 사용
4. @Inherited<br/>
어노테이션도 자손 클래스에 상속하고자 할 때 사용
5. Repeatable<br/>
반복해서 붙일 수 있는 어노테이션을 정의할 때 사용

#### 어노테이션 생성하기
```Java
@interface 이름 {
    타입 요소 이름();
        ...
}
```
```Java
@interface DateTime{
	String yymmdd();
    String hhmmss();
}

@interface TestInfo{
	int count() default 1;
    String testedBy();
    TestType testType();
    DateTime testDate();
}


@TestInfo{
	testedBy="Kim",
    testTools={"JUnit", "AutoTester"},
    testType=TestType.FIRST,
    testDate=@DateTime(yymmdd="210922", hhmmss="211311")
)// count를 생략했으므로 default인 "count=1"이 적용된다.
public class NewClass{...}
```
#### 특징
- default를 사용해 기본값을 지정할 수 있다.
- 요소가 하나이고 이름이 value일 때는 요소의 이름을 생략할 수 있다.
```Java
@interface TestInfo{
	String value();
}
@TestInfo("passed") // value="passed"와 동일
class NewClass{...}
```
- 요소의 타입이 배열인 경우 괄호{}를 사용해야 한다.
```Java
@interface TestInfo{
	String[] testTools();
}

@TestInfo(testTools={"JUnit", "AutoTester"})
@TestInfo(testTools="JUnit") // 요소가 1개일 때는 {}를 사용하지 않아도 된다.
@TestInfo(testTool={}) // 요소가 없으면 {}를 써넣어야 한다.
```
### 모든 어노테이션의 조상
Annotation은 모든 어노테이션의 조상이지만 상속은 불가능하다.
```Java
public interface Annotation{
	boolean equals(Object obj);
    int hashCode();
    String toString();
    
    Class<? extends Annotation> annotationType();
    }
```
### 마커 어노테이션
요소가 하나도 정의되지 않은 어노테이션
### 어노테이션 규칙
- 요소의 타입은 기본형, String, enum, 어노테이션, 클래스만 허용된다.
- 괄호() 안에 매개변수를 선언할 수 없다.
- 예외를 선언할 수 없다.
- 요소의 타입을 매개변수로 정의할 수 없다.(\<T\>)

```Java
@interface AnnoConfigTest{
    int id = 100; // 상수 ok
    String major(int i, int j) //매개변수 x
    String minor() throws Exception; // 예외 x
    ArrayList<T> list(); // 요소의 타입을 매개변수 x
```
## 어노테이션 기반 개발
### @Component - 스프링 빈 식별하기
클래스가 스프링 빈 (스프링 컴포넌트라고 부르기도 함)을 표현한다는 사실을 나타내는 어노테이션<br/>
애플리케이션에서는 기능에 따라 특별한 @Component를 사용하도록 권장한다. ex) @Controller, @Service, @Repository <br/>
이러한 에너테이션들은 @Component로 메타 에너테이션 되어있다.
```Java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {
    String value() default "";
}
```
```Java
package sample.spring.chapter06.bankapp.service;

import org.springframework.stereotype.Service;

@Service(value="fixedDepositService")
public class FixedDepositServiceImpl implements FixedDepositService {
```
스프링 컨테이너에 빈을 어떤 이름으로 등록할지 지정하는 value 속성을 받는다.<br/>
value 속성을 명시하지 않아도 이름을 지정할 수 있다. (어노테이션의 특징)<br/>
bean 엘리먼트의 id속성과 같은 역할을 한다.<br/>
빈 이름을 지정하지 않으면 스프링은 클래스 이름에서 첫 번째 글자를 소문자로 바꾼 이름을 빈 이름으로 사용한다.<br/>
스프링의 클래스경로 스캐닝을 활성화하면 스프링 컨테이너는 @Component, @Controller, @Service, @Repository를 설정한 빈 클래스를  자동으로 등록한다.
> 스프링 context 스키마에 \<component-scan\> 엘리먼트를 사용한다.
``` Xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context 
        http://www.springframework.org/schema/context/spring-context.xsd">

	<context:component-scan base-package="sample.spring"/>
</beans>
```
스프링 context 스키마에 대한 참조를 포함해 스키마 내부 엘리먼트를 사용할 수 있다.<br/>
- base-package : 해당 패키지와 그 하위 패키지에서 스프링 빈을 검색한다.<br/>
콤마를 통해 검색할 패키지를 나열할 수 있다.
- resource-pattern : 자동 등록시 고려할 빈 클래스를 걸러낼 때 사용<br/>
디폴트값 **/*. : base-package 속성으로 지정한 패키지 아래의 모든 클래스를 자동 등록 대상으로 한다.
- 하위 엘리먼트 \<include-filter\>와 \<exclude-filter\>를 사용해 자동 등록에 포함할 컴포넌트 클래스와 제외할 컴포넌트 클래스를 지정할 수 있다.
```Xml
<beans ...>
    <context:component-scan base-package="sample.example">
        <context:include-filter type="annotation" expression="example.annotation.MyAnnotation"/>
        <context:exclude-filter type="regex" expression=".*Details"/>
    </context:component-scan>
</beans>
```
type 속성 : 빈 클래스를 걸러낼 때 사용할 전략을 지정<br/>
expression 속성 : 걸러낼 때 사용할 식을 지정<br/>
![KakaoTalk_20220501_185951105](https://user-images.githubusercontent.com/25950908/166141164-7495b0bb-b8cd-48e9-8019-7cc0c69d9ed0.jpg)
### @Autowired - 객체의 타입으로 의존 관계 자동 연결하기
#### 필드 수준에서 @Autowired 사용하기
```Java
package sample.spring.chapter06.bankapp.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sample.spring.chapter06.bankapp.dao.AccountStatementDao;
import sample.spring.chapter06.bankapp.domain.AccountStatement;

@Service(value="accountStatementService")
public class AccountStatementServiceImpl implements AccountStatementService {

	@Autowired 
	private AccountStatementDao accountStatementDao;
	
	@Override
	public AccountStatement getAccountStatement(Date from, Date to) {
		return accountStatementDao.getAccountStatement(from, to);
	}
}
```
AccountStatementServiceImpl를 생성할 때 스프링의 AutowiredAnnotationBeanPostProcessor(BeanPostProcessor를 구현함)가 accountStatmentDao 필드를 자동 연결한다.<br/> AutowiredAnnotationBeanPostProcessor는 스프링 컨테이너에서 AccountStatementDao 타입 빈을 얻어서 accountStatementDao 필드에 대입한다.<br/>
@Autowired를 설정한 필드나 필드에 대응하는 세터 메서드가 꼭 public이어야 할 필요는 없다.
#### 메서드 수준에서 @Autowired 사용하기
```Java
package sample.spring.chapter06.bankapp.service;

@Service("customerRegistrationService")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomerRegistrationServiceImpl implements CustomerRegistrationService {

	private CustomerRegistrationDetails customerRegistrationDetails;

	@Autowired
	private CustomerRegistrationDao customerRegistrationDao;
    .....
	@Autowired
	public void obtainCustomerRegistrationDetails(CustomerRegistrationDetails customerRegistrationDetails) {
		this.customerRegistrationDetails = customerRegistrationDetails;
	}
    .....
	@Override
	public void setAccountNumber(String accountNumber) {
		customerRegistrationDetails.setAccountNumber(accountNumber);
	}
    .....
}
```
obtainCustomerRegistrationDetails 메서드에 @Autowired를 설정하면 메서드 인수가 자동 연결된다.<br/>
CustomerRegistrationDetails는 타입을 통해 자동 연결된다.
> 빈 인스턴스가 생성된 다음에 @Autowired를 설정한 메서드가 자동으로 호출되고, @Autowired를 설정한 필드에는 일치하는 빈 인스턴스가 주입된다.
#### 생성자 수준에서 @Autowired 사용하기
```Java
package sample.spring.chapter06.bankapp.service;

@Service(value = "customerRequestService")
public class CustomerRequestServiceImpl implements CustomerRequestService {
	private CustomerRequestDetails customerRequestDetails;
	private CustomerRequestDao customerRequestDao;

	@Autowired
	public CustomerRequestServiceImpl(
			CustomerRequestDetails customerRequestDetails,
			CustomerRequestDao customerRequestDao) {
		logger.info("Created CustomerRequestServiceImpl instance");
		this.customerRequestDetails = customerRequestDetails;
		this.customerRequestDao = customerRequestDao;
	}

	@Override
	public void submitRequest(String requestType, String requestDescription) {
		// -- create an instance of UserRequestDetails and save it
		customerRequestDao.submitRequest(customerRequestDetails);
	}

}
```
생성자에 @Autowired를 설장하면 생성자 인수가 자동 연결된다.
> 스프링 4.3부터 빈 클래스에 생성자가 단 하나만 있는 경우, 이 유일한 생성자에 @Autowired를 설정하지 않아도 디폴트로 자동 연결을 수행한다.

@Autowired에서 자동 연결에 필요한 타입과 일치하는 빈을 찾을 수 없으면 예외가 발생한다.<br/>

required 속성을 통해 의존 관계가 필수적인지 여부를 지정할 수 있다.<br/>
required가 false인 경우 필요한 타입을 스프링 컨테이너 안에서 찾을 수 없어도 예외가 발생하지 않는다.<br/>
required 속성의 디폴트 값은 true다.

빈 클래스의 생성자에 required 속성이 true인 @Autowired를 설정하면 다른 생성자에 @Autowired를 설정할 수 없다.<br/>
required 속성이 false인 @Autowired 어노테이션을 설정한 생성자가 둘 이상인 경우, 스프링은 생성자 중 하나를 호출해 빈 클래스 인스턴스를 생성한다.<br/>
만족하는 의존 관계 개수가 가장 큰 생성자를 선택하고, 의존 관계를 모두 찾을 수 없는 경우 디폴트 생성자를 호출한다.

### @Qualifier - 빈 이름으로 의존 관계 자동 연결하기
@Autowired와 함께 사용하면 의존 관계를 빈 이름으로 자동 연결할 수 있다.
@Qualifier도 필드 수준, 메서드 수준, 생성자 수준에서 사용 한다.
```Java
package sample.spring.chapter06.bankapp.service;

@Service(value="fixedDepositService")
@Qualifier("service")
public class FixedDepositServiceImpl implements FixedDepositService {
	
	@Autowired
	@Qualifier(value="myFixedDepositDao")
	private FixedDepositDao myFixedDepositDao;
	.....
}
```
@Qualifier의 value 속성은 (필드, 메서드 인수, 생성자 인수)에 인스턴스를 대입할 빈의 이름을 지정한다.<br/>
스프링은 먼저 @Autowired를 설정한 필드, 생성자 인수, 메서드 인수의 객체의 타입으로 후보 빈을 찾는다.<br/>
그 후 스프링은 @Qualifier를 사용해 자동 연결 후보 목록에서 유일한 빈을 구별한다.
```Java
public class Sample {
	@Autowired
	public Sample(@Qualifier("aBean") ABean bean) { .... }

	@Autowired
	public void doSomething(@Qualifier("bBean") BBean bean, CBean cBean) { ..... }
}
```
생성자 인수로 타입이 ABean이고 빈 이름이 aBean인 빈을 찾는다.<br/>
메서드 인수로 BBean 타입의 이름이 bBean인 빈은 빈 이름으로 자동 연결되지만, CBean의 의존관계는 객체의 타입으로 자동 연결된다.<br/>
빈 이름 대신 지정자를 사용해 빈 의존 관계를 자동 연결할 수도 있다.
### 지정자를 사용해 빈 자동 연결하기
지정자 : @Qualifier를 사용해 빈을 연결할 때 사용하는 문자열<br/>
```Java
package sample.spring.chapter06.bankapp.dao;

@Repository(value = "txDao")
@Qualifier("myTx")
public class TxDaoImpl implements TxDao {

	@Override
	public List<Tx> getTransactions(int accountNumber) {
		List<Tx> txList = new ArrayList<Tx>();
		txList.add(new Tx(1, "High value money transfer", "complete"));
		txList.add(new Tx(2, "High value money transfer", "in process"));
		return txList;
	}
}
```
myTx는 txDao의 지정자이고, txDao는 빈 이름이다.
```Java
package sample.spring.chapter06.bankapp.service;

@Service("txService")
@Qualifier("service")
public class TxServiceImpl implements TxService {
	@Autowired
	@Qualifier("myTx")
	private TxDao txDao;
	.....
}
```
txDao를 txDao 빈에 연결하는 대신, 지정자 값이 myTx인 빈에 연결하라고 지정한다.
#### 타입 지정 컬렉션(typed collection) - 지정자와 연관된 모든 빈을 자동 연결하기
```Java
package sample.spring.chapter06.bankapp.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Services {
	@Autowired
	@Qualifier("service")
	private Set<MyService> services;

	public Set<MyService> getServices() {
		return services;
	}
}
```
Set\<MyService\> 컬렉션은 MyService 인터페이스를 구현하는 모든 서비스를 표현한다.<br/>
@Qualifier는 지정자 값(service)와 연관된 모든 빈을 Set\<MyService\> 컬렉션에 넣어준다.<br/>
@Qualifier("service") 애너테이션이 설정된 모든 서비스는 Set\<MyService\>에 자동 연결된다.

### 커스텀 지정자 어노테이션 만들기
![KakaoTalk_20220502_151102497](https://user-images.githubusercontent.com/25950908/166192060-454733cf-957d-4d26-9058-7b60a75be69a.jpg)
자금 이체를 즉시할지, 같은 은행으로 이체할지 여부에 따라 적절한 서비스를 선택한다.
```Java
package sample.spring.chapter06.bankapp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface FundTransfer {
	TransferSpeed transferSpeed();
	BankType bankType();
}
```
```Java
package sample.spring.chapter06.bankapp.annotation;

public enum BankType {
	SAME, DIFFERENT
}
```
```Java
package sample.spring.chapter06.bankapp.annotation;

public enum TransferSpeed {
	IMMEDIATE, NORMAL
}
```
FundTransfer 어노테이션을 선언한다. 메타 어노테이션으로 @Qualifier가 지정되어 있다.<br/>
해당 어노테이션이 커스텀 지정자 어노테이션이라는 뜻이다.<br/>
이를 지정하지 않으면, 스프링 CustomAutowireConfigurer빈(BeanFactoryProcessor 타입)을 사용해 명시적으로 @FundTransfer를 등록해야 한다.
```Java
package sample.spring.chapter06.bankapp.service;

@FundTransfer(transferSpeed = TransferSpeed.IMMEDIATE, bankType=BankType.SAME)
@Service
public class ImmediateSameBank implements FundTransferService {
	.....
}
```
```Java
package sample.spring.chapter06.bankapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sample.spring.chapter06.bankapp.annotation.BankType;
import sample.spring.chapter06.bankapp.annotation.FundTransfer;
import sample.spring.chapter06.bankapp.annotation.TransferSpeed;
import sample.spring.chapter06.bankapp.domain.Account;

@Component
public class FundTransferProcessor {
	@Autowired
	@FundTransfer(transferSpeed = TransferSpeed.IMMEDIATE, bankType = BankType.SAME)
	private FundTransferService sameBankImmediateFundTransferService;
	
	@Autowired
	@FundTransfer(transferSpeed = TransferSpeed.IMMEDIATE, bankType = BankType.DIFFERENT)
	private FundTransferService diffBankImmediateFundTransferService;

	public void sameBankImmediateFundTransferService() {
		sameBankImmediateFundTransferService.transferFunds(new Account(), new Account());
	}
	
	public void diffBankImmediateFundTransferService() {
		diffBankImmediateFundTransferService.transferFunds(new Account(), new Account());
	}
	
}
```
FundTransferProcessor는 @FundTransfer 애너테이션을 사용해 여러 다른 FundTransferService 구현을 필드에 자동 연결한다.
### JSR 330 @Inject와 @Named 어노테이션
JSR 330은 자바 플랫폼상의 의존 관계 주입 어노테이션을 표준화한다.<br/>
@Inject와 @Named는 @Autowired와 @Quailifier와 비슷하고 스프링은 @Inject와 @Named를 지원한다.<br/>
@Inject는 @Autowired와 뜻이 같다. 객체의 타입으로 의존 관계를 자동 연결한다.<br/>
메서드 수준, 생성자 수준, 필드 수준에서 사용 가능하다.<br/>
생성자 의존 관계 주입 -> 필드 주입 -> 메서드 주입 순으로 일어난다.<br/>
@Named를 타입 수준에서 사용하면 스프링 @Component 어노테이션과 마찬가지로 작동한다.<br/>
@Named를 메서드 파라미터 수준에서 사용하거나 생성자 인수 수준에서 사용하면 스프링 @Qualifier 어노테이션처럼 작동한다.<br/>
@Named나 @Inject를 사용하려면 JSR 330 JAR 파일을 프로젝트에 포함시켜야 한다.<br/>
pom.xml 파일의 \<dependency\> 엘리먼트에 JSR 330 JAR 파일을 포함시킨다.
```XML
<dependency>
	<groupId>javax.inject</groupId>
	<artifactId>javax.inject</artifactId>
	<version>1</version>
</dependency>
```
@Inject에는 @Autowired의 required 속성과 같은 역할을 하는 부분이 없지만, 자바8의 Optional 타입을 사용하면 똑같은 동작을 수행할 수 있다.
```Java
import java.util.Optional;
...
@Named(value="myService")
public class MyService {
	@Inject
	private Optional<ExternalService> externalServiceHolder;

	public void doSomething(Data data) {
		if(externalServiceHolder.isPresent()) {
			externalServiceHolder.get().save(data);
		}
		else {
			saveLocally(data);
		}
	}
	private void saveLocally(Data data) {
		.....
	}
}
```
### JSR 250 @Resource 어노테이션
필드와 세터 메서드를 빈 이름으로 자동 연결하도록 지원한다.<br/>
CommonAnnotationBeanPostProcessor(BeanPostProcessor 인터페이스 구현)가 @Resource 어노테이션을 처리한다.<br/>
```Java
import javax.annotation.Resource;

@Named("fixedDepositService")
public class FixedDepositServiceImpl implements FixedDepositService {

	@Resource(name="myFixedDepositDao")
	private FixedDepositDao myFixedDepositDao;
}
```
빈 이름으로 자동 연결해야 하는 경우 @Autowired와 @Qualifier 어노테이션 대신 @Resource를 사용해야 한다.<br/>
@Autowired와 @Qualifier 의 조합으로 빈 이름으로 자동 연결을 시도할 때 : 필드 타입과 일치하며 자동 연결할 수 있는 빈을 찾고, 그 후에 @Qualifier로 지정한 이름을 사용해 후보 빈을 줄인다.<br/>
@Resource를 사용할 때 : name 속성값에 지정한 이름을 사용해 단 하나의 빈을 찾아낸다. -> @Resource를 처리할 때는 자동 연결할 필드 타입을 고려하지 않는다.<br/>
name 속성을 지정하지 않으면 필드나 프로퍼티 이름을 name 속성의 기본 값으로 사용한다.<br/>
해당 이름인 빈이 없으면 스프링 컨테이너는 타입을 통해 빈을 찾는다.

### @Scope, @Lazy, @DependsOn, @Primary 어노테이션
- @Scope : value 속성으로 빈 스코프를 지정한다.</br>
org.springframework.beans.factory.config.ConfigurableBeanFactory에 prototype과 singleton을 상수로 정의했다.<br/>
WebApplicationContext 인터페이스에 SCOPE_* 상수로 application, session, request 스코프에 대한 상수를 정의했다.<br/>
value 대신 scopeName 속성을 사용할 수도 있다. -> 스프링 4.2 이후 어노테이션의 value 속성을 더 의미 있는 이름으로 가리킬 수 있는 속성 별명이 추가됐다.
- @Lazy : value를 true로 하거나 지정하지 않으면 (@Lazy라고만 쓰면) 빈을 나중에 초기화하는 것으로 간주한다.<br/>
이를 지연 자동 연결(lazy autowire) 의존 관계에 사용할 수도 있다.
- @DependsOn : 암시적으로 빈 의존 관계를 지정할 수 있다.<br/>
Sample 클래스를 생성하기 전에 beanA와 beanB 빈을 생성하라고 스프링 컨테이너에 지시한다.
```Java
@DependsOn(value = {"beanA", "beanB"})
@Component
public class Sample{ ..... }
```
- @Primary : 자동 연결할 수 있는 후보가 여럿 있을 때, 한 빈을 자동 연결의 제1 후보로 지정할 수 있다.

### @Value 어노테이션
빈에 필요한 설정 정보를 XML을 이용하지 않고 지정한다.<br/>
필드 수준, 메서드 수준, 메서드 파라미터 수준, 생성자 인수 수준에서 사용 가능하다.<br/>
문자열 대신 SpEL 식을 지정할 수도 있다.
### SpEL
실행 시점에 객체에 대한 질의를 수행하고 객체를 조작할 때 사용하는 표현 언어이다.
