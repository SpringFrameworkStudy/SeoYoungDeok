# 1주차. 스프링 프레임워크

`스프링 프레임워크` : 자바 엔터프라이즈 애플리케이션 개발을 단순하게 해주는 오픈소스 애플리케이션 프레임워크

스프링의 중심에는 IOC 컨테이너가 있다.</br>
IOC 컨테이너는 DI 기능을 제공한다.</br>
스프링 IoC 컨테이너를 *스프링 컨테이너* 라고도 한다.

## 스프링 IoC 컨테이너
`의존 관계` : 객체가 다른 객체와 상호작용하는 경우를 객체의 의존 관계라고 한다.</br>
X 객체가 Y, Z 객체와 상호작용한다면 X 객체는 Y, Z 객체와 의존 관계이다.</br>
`DI (의존 관계 주입, Dependency Injection)` : 객체 간의 의존 관계를 **생성자 인수**나 **세터 메서드 인수**로 명시하고 객체를 생성할 때 생성자나 세터를 통해 의존 관계를 주입하는 방식을 따르는 디자인 패턴</br>
`빈 (Bean)` : 스프링 컨테이너가 생성하고 관리하는 애플리케이션 객체들</br>
의존 관계를 만들고 주입하는 책임은 애플리케이션의 객체가 아닌 스프링 컨테이너에 있어 DI를 제어의 역전(IoC)이라고도 부른다.

### FixedDepositController 클래스
``` Java
public class FixedDepositController {
    private FixedDepositService fixedDepositService;
    
    public FixedDepositController() {
        fixedDepositService = new FixedDepositService();
    }

    public boolean submit() {
        // 정기 예금의 상세정보 저장
        fixedDepositService.save(.....);
    }
}
```
FixedDepositController를 스프링 빈으로 설정하려면 컨트롤러가 서비스에 대한 의존 관계를 생성자 인수로 받거나 세터 메서드 인수로 받게 수정해야 한다.

### 생성자 인수를 활용한 DI
``` Java
public class FixedDepositController {
    private FixedDepositService fixedDepositService;
    
    public FixedDepositController(FixedDepositService fixedDepositService) {
        this.fixedDepositService = fixedDepositService;
    }

    public boolean submit() {
        // 정기 예금의 상세정보 저장
        fixedDepositService.save(.....);
    }
}
```
### 세터 메서드 인수를 활용한 DI
``` Java
public class FixedDepositController {
    private FixedDepositService fixedDepositService;
    
    public FixedDepositController() {

    }

    public void setFixedDepositService(FixedDepositService fixedDepositService) {
        this.fixedDepositService = fixedDepositService;
    }
    public boolean submit() {
        // 정기 예금의 상세정보 저장
        fixedDepositService.save(.....);
    }
}
```

스프링 IoC 컨테이너는 애플리케이션의 *설정 메타데이터*를 읽어서 애플리케이션 객체들과 그들의 의존 관계를 인스턴스화한다.

### XML 형식으로 지정한 설정 메타데이터 
```XML
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- property는 세터 메서드 인수를 활용한 DI일 때 사용-->
	<bean id="controller"
		class="sample.spring.chapter01.bankapp.FixedDepositController">
		<property name="fixedDepositService" ref="service" />
	</bean>

    <!-- constructor-arg는 생성자 인수를 활용한 DI일 때 사용 -->
    <bean id="controller"
		class="sample.spring.chapter01.bankapp.FixedDepositController">
		<constructor-arg ref="service" />
	</bean>
    

	<bean id="service"
		class="sample.spring.chapter01.bankapp.FixedDepositService">
		<property name="fixedDepositDao" ref="dao" />
	</bean>

	<bean id="dao" class="sample.spring.chapter01.bankapp.FixedDepositDao" />
</beans>
```
각 bean 엘리먼트는 스프링 컨테이너가 관리하는 애플리케이션 객체를 정의하고, property, constructor-arg 엘리먼트를 세터 메서드, 생성자 인수로 전달해 의존 관계를 주입한다.</br>
스프링 컨테이너는 자바 리플렉션 API를 사용해 객체를 만들고 의존 관계를 주입한다.</br>
[리플렉션API](https://tecoble.techcourse.co.kr/post/2020-07-16-reflection-api/) : 구체적인 클래스 타입을 알지 못해도, 그 클래스의 정보에 접근할 수 있게 해주는 자바 API

스프링 컨테이너를 통해 트랜잭션 관리, 보안, 원격 접근 등 엔터프라이즈 서비스를 투명하게 객체에 적용할 수 있다.</br>
스프링 컨테이너가 애플리케이션 객체에 추가 기능을 부여하고 애플리케이션 객체를 *평범한 자바 객체*(POJO, Plane Old Java Object)로 모델링 할 수 있다.

## 스프링의 장점

1. 객체 생성과 의존 관계 주입을 처리함으로써 자바 애플리케이션 조합을 쉽게 만들어준다.
2. 스프링은 POJO로 애플리케이션을 개발하는 것을 권장한다.

## 스프링 DI 기능을 사용하는 애플리케이션의 5단계

1. 애플리케이션에 쓰이는 여러 객체와 객체 간 의존 관계를 파악한다.
![KakaoTalk_20220413_160018605](https://user-images.githubusercontent.com/25950908/163119060-a81838cb-244a-4578-9edf-c37222dc5885.jpg)
2. 1단계에서 파악한 각 애플리케이션 객체에 상응하는 POJO 클래스를 만든다.
```Java
package sample.spring.chapter01.bankapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FixedDepositController {
	private static Logger logger = LogManager
			.getLogger(FixedDepositController.class);

	private FixedDepositService fixedDepositService;

	public FixedDepositController() {
		logger.info("initializing");
	}

	public void setFixedDepositService(FixedDepositService fixedDepositService) {
		logger.info("Setting fixedDepositService property");
		this.fixedDepositService = fixedDepositService;
	}

	public boolean submit() {
		return fixedDepositService.createFixedDeposit(new FixedDepositDetails(
				1, 10000, 365, "someemail@something.com"));
	}

	public FixedDepositDetails get() {
		return fixedDepositService.getFixedDepositDetails(1L);
	}
}
```
3. 애플리케이션 객체 간 의존 관계를 표현하는 설정 메타데이터를 만든다.

설정 메타데이터는 애플리케이션에 필요한 엔터프라이즈 서비스 정보를 지정한다.</br>
ex) 트랜잭션 관리를 위해서는 설정 메타데이터에서 PlatformTransactionManager 인터페이스를 구현하고 설정해야 한다.</br>
> 설정 메타데이터를 지정하는 방법

    1. XML 파일을 사용
    2. 애너테이션 : POJO 클래스에 애너테이션을 설정
    3. 자바 코드 : @Configuration 애너테이션을 설정한 자바 클래스를 사용 (스프링 3.0 이후)

``` XML
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

	<bean id="controller"
		class="sample.spring.chapter01.bankapp.FixedDepositController">
		<property name="fixedDepositService" ref="service" />
	</bean>

	<bean id="service"
		class="sample.spring.chapter01.bankapp.FixedDepositService">
		<property name="fixedDepositDao" ref="dao" />
	</bean>

	<bean id="dao" class="sample.spring.chapter01.bankapp.FixedDepositDao" />
</beans>
```
- beans 엘리먼트는 XML 파일의 루트 엘리먼트이고, beans.xsd스키마에서 정의한다.
- 각 bean 엘리먼트는 스프링 컨테이너가 관리할 애플리케이션 객체를 설정한다.</br>
bean 엘리먼트를 빈 정의라고 부르고, 빈 정의에 따라 만들어내는 객체를 빈이라고 부른다.</br>
id : 빈의 유일한 이름</br>
class : 빈 클래스의 full-name</br>
name : 빈에 별명(alias)을 지정할 수 있다.</br>
property 엘리먼트는 bean 엘리먼트가 설정하는 빈의 의존 관계를 지정한다. 스프링 컨테이너가 의존 관계를 설정하기 위해 호출할 자바빈 스타일 세터 메서드와 대응된다.</br>
ref 속성은 설정 메타데이터 안에 있는 다른 빈을 가리킨다.(다른 빈의 id 또는 name과 일치해야 함)

의존중인 객체 (컨트롤러) 의존 대상(서비스) 자바빈 스타일 세터(세터)
컨테이너는 ref가 가리키는 의존 대상(서비스) 클래스의 인스턴스를 만들고, 의존중인 객체(컨트롤러)의 세터를 호출하면서 서비스 인스턴스를 전달한다.

![KakaoTalk_20220413_164854485](https://user-images.githubusercontent.com/25950908/163126930-5a99d49b-e48f-4efc-8390-4dc45f32034a.jpg)</br>
세터 메서드를 호출하기 전에 빈이 의존하는 다른 빈들이 완전히 설정되도록 보장한다.</br>

4. 스프링 IoC 컨테이너의 인스턴스를 만들고 설정 메타데이터를 인스턴스에 전달한다.</br>
스프링 ApplicationContext 객체는 스프링 컨테이너 인스턴스를 표현한다.</br>
Application 인터페이스 구현 방법은 ClassPathXmlApplicationContext, FileSystemXmlApplicationContext, XmlWebApplicationContext, AnnotationConfigWebApplicationContext 등이 존재하고, 설정 메타데이터의 정의 방법, 애플리케이션의 유형에 따라 어떤 ApplicationContext를 선택할 지 달라진다.
5. 스프링 IoC 컨테이너 인스턴스로 애플리케이션 객체에 접근한다.
애플리케이션 객체 인스턴스는 ApplicationContext 인터페이스의 getBean 메서드 중 하나를 호출해서 접근할 수 있다.

```Java
public class BankApp {
	private static Logger logger = LogManager.getLogger(BankApp.class);

	
	public static void main(String args[]) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:META-INF/spring/applicationContext.xml");
		FixedDepositController fixedDepositController = (FixedDepositController) context.getBean("controller");

		logger.info("Submission status of fixed deposit : " + fixedDepositController.submit());
		logger.info("Returned fixed deposit info : " + fixedDepositController.get());
	}
}
```
main 메서드가 스프링 컨테이너로부터 컨트롤러 빈을 가져와서 빈에 있는 메서드를 호출한다. getBean의 인수는 가져오려는 빈의 이름(id나 name)이다. 스프링 컨테이너에 등록된 이름과 지정한 이름이 일치하는 빈을 찾을 수 없으면 예외를 발생시킨다.

![image](https://user-images.githubusercontent.com/25950908/163127340-f691b149-fcdf-46c2-9b7f-e216c4f32a42.png)
스프링 컨테이너가 applicationContext.xml에 정의된 각 빈의 인스턴스를 생성한다는 것을 보여준다.

------------------------------
## '인터페이스를 사용하는 프로그래밍' 설계 원칙

의존 중인 클래스가 의존 관계의 구체적인 클래스를 직접 참조한다면, 두 클래스 사이에 긴밀한 결합(*coupling*)이 생긴다.
> 의존 관계의 구현을 변경하려면 의존 중인 클래스도 변경해야 한다.

자바 인터페이스는 구현 클래스가 준수해야 하는 계약을 정의한다.</br>
따라서 클래스가 의존 관계를 구현하는 인터페이스에 의존한다면, 해당 의존 관계의 구현을 변경하더라도 의존 중인 클래스를 변경할 필요가 없다.</br>
> 인터페이스를 사용하는 프로그래밍을 통해 의존 중인 클래스와 의존 관계 사이에 느슨한 결합을 만든다.

[느슨한 결합과 강한 결합](https://hongjinhyeon.tistory.com/141)
</br>
![KakaoTalk_20220416_153059622](https://user-images.githubusercontent.com/25950908/163664599-885c740a-bce2-4c90-88f4-6e141a8c5bbf.jpg)

![KakaoTalk_20220416_153059373](https://user-images.githubusercontent.com/25950908/163664607-110162b0-f385-4103-85b0-2d49a01800fb.jpg)
서비스가 Dao 인터페이스에 의존하고, 각 클래스가 Dao 인터페이스를 구현한다. </br>
Dao 인터페이스는 JDBC와 Hibernate중 어느 쪽을 쓰고 싶은가에 따라 두 클래스 중 하나를 서비스 인스턴스에 공급한다.</br>
서비스가 직접적으로 클래스에 의존하지 않기 때문에 데이터베이스와 상호 작용하는 전략을 바꾸기 위해 서비스 클래스를 바꿀 필요가 없다.</br>
다른 데이터베이스와 상호 작용 전략을 선택하는 것도 Dao 인터페이스를 구현하는 새로운 클래스의 인스턴스를 서비스에 공급하는 식으로 쉽게 할 수 있다.

### 의존 중인 클래스의 테스트성 향상

서비스 클래스의 단위 테스트를 단순화하기 위해 데이터베이스를 쓰지 않는 FixedDepositDao 인터페이스에 목(*mock*) 구현으로 구체적 클래스를 대신할 수 있다.</br>
의존 중인 클래스를 단위 테스트하기 위해 인프라를 설치하려는 노력을 줄일 수 있다.

## 스프링 애플리케이션에서 인터페이스를 사용하는 프로그래밍 설계 방식 사용하기

1. 의존 관계에 구체적인 구현 클래스가 아닌 의존 관계 인터페이스를 참조하는 빈 클래스를 만든다.

![KakaoTalk_20220416_162944278](https://user-images.githubusercontent.com/25950908/163666143-7e34ee1c-5fa2-4b5d-af63-9f4bdeb8124f.jpg)

### FixedDepositServiceImpl 클래스
```Java
package sample.spring.chapter02.bankapp.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sample.spring.chapter02.bankapp.dao.FixedDepositDao;
import sample.spring.chapter02.bankapp.domain.FixedDepositDetails;

public class FixedDepositServiceImpl implements FixedDepositService {
	private static Logger logger = LogManager.getLogger(FixedDepositServiceImpl.class);

	private FixedDepositDao fixedDepositDao; // 구체적인 구현 클래스가 아닌 FixedDepositDao 인터페이스에 의존

	public FixedDepositServiceImpl() {
		logger.info("initializing");
	}

	public FixedDepositDao getFixedDepositDao() {
		return fixedDepositDao;
	}

	public void setFixedDepositDao(FixedDepositDao fixedDepositDao) {
		logger.info("Setting fixedDepositDao property");
		this.fixedDepositDao = fixedDepositDao;
	}

	public FixedDepositDetails getFixedDepositDetails(long id) {
		return fixedDepositDao.getFixedDepositDetails(id);
	}

	public boolean createFixedDeposit(FixedDepositDetails fdd) {
		return fixedDepositDao.createFixedDeposit(fdd);
	}
}
```

2. 의존 중인 빈에 주입하고 싶은 구체적인 구현을 지정할 수 있는 bean 엘리먼트를 정의한다.

```Xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.2.xsd">

	<bean id="controller"
		class="sample.spring.chapter02.bankapp.controller.FixedDepositControllerImpl">
		<property name="fixedDepositService" ref="service" />
	</bean>

	<bean id="service"
		class="sample.spring.chapter02.bankapp.service.FixedDepositServiceImpl">
		<property name="fixedDepositDao" ref="dao" />
	</bean>

	<bean id="dao"
		class="sample.spring.chapter02.bankapp.dao.FixedDepositHibernateDao" />
</beans>
```
인터페이스의 어떤 구현을 주입할지 XML 파일로 지정할 수 있다.</br>
빈 정의의 class 속성을 사용할 구현 클래스의 전체 이름으로 지정한다.

## 정적 팩토리 메서드나 인스턴스 팩토리 메서드를 사용해 빈 만들기

스프링 객체가 관리할 객체를 반환받기 위해 다음과 같은 방법을 사용할 수 있다.

### 정적 팩토리 메서드로 빈 초기화 하기
[정적 팩토리 메서드](https://velog.io/@ljinsk3/%EC%A0%95%EC%A0%81-%ED%8C%A9%ED%86%A0%EB%A6%AC-%EB%A9%94%EC%84%9C%EB%93%9C%EB%8A%94-%EC%99%9C-%EC%82%AC%EC%9A%A9%ED%95%A0%EA%B9%8C)
#### FixedDepositDaoFactory
```Java
public class FixedDepositDaoFactory {
	private FixedDepositDaoFactory() {}

	public static FixedDepositDao getFixedDepositDao(String daoType, ...) {
		FixedDepositDao fixedDepositDao = null;

		if("jdbc".equalsIgnoreCase(daoType)) {
			fixedDepositDao = new FixedDepositJdbcDao();
		}
		if("hibernate".equalsIgnoreCase(daoType)) {
			fixedDepositDao = new FixedDepositHibernateDao();
		}
		.....
		return fixedDepositDao;
	}
}
```
FixedDepositDaoFactory 클래스는 daoType의 인수값에 따라 FixedDepositDao 인터페이스의 구현 클래스 중 하나의 인스턴스를 만들어내는 getFixedDepositDao 정적 메서드를 정의한다.</br>
```XML
<bean id="service"
	class="sample.spring.chapter02.bankapp.service.FixedDepositServiceImpl">
	<property name="fixedDepositDao" ref="dao" />
</bean>
<bean id="dao" class="sample.spring.FixedDepositDaoFactory"
	factory-method="getFixedDepositDao">
	<constructor-arg index="0" value="jdbc"/>
	.....
</bean> 
```
class 속성은 정적 팩토리 매서드를 정의하는 클래스의 전체 이름으로 지정한다.</br>
factory-method 속성은 정적 팩토리 메서드의 이름을 지정한다.</br>
constructor-arg 엘리먼트는 생성자에게 인수를 넘기거나, 정적 팩토리 메서드 또는 인스턴스 팩토리 메서드에 인수를 넘길 때 사용한다.</br>
dao 빈을 얻기 위해 ApplicationContext의 getBean 메서드를 호출하면 FixedDepositDaoFactory의 getFixedDepositDao 팩토리 메서드를 호출한다.</br>
따라서 getBean("dao")를 호출하면 FixedDepositDaoFactory 클래스의 인스턴스가 아닌 getFixedDepositDao 팩토리 메서드가 생성한 FixedDepositDao 인스턴스를 반환한다.</br>

### 인스턴스 팩토리 메서드를 호출해 빈 초기화하기
#### FixedDepositDaoFactory
```Java
public class FixedDepositDaoFactory {
	public FixedDepositDaoFactory() {}

	public static FixedDepositDao getFixedDepositDao(String daoType, ...) {
		FixedDepositDao fixedDepositDao = null;

		if("jdbc".equalsIgnoreCase(daoType)) {
			fixedDepositDao = new FixedDepositJdbcDao();
		}
		if("hibernate".equalsIgnoreCase(daoType)) {
			fixedDepositDao = new FixedDepositHibernateDao();
		}
		.....
		return fixedDepositDao;
	}
}
```
public 생성자가 정의된 점이 다르다.
```XML
<bean id="daoFactory" class="sample.spring.chapter02.bankapp.FixedDepositDaoFactory"/>

<bean id="dao" factory-bean="daoFactory" factory-method="getFixedDepositDao">
	<constructor-arg index="0" value="jdbc"/>
</bean>

<bean id="service"
	class="sample.spring.chapter02.bankapp.service.FixedDepositServiceImpl">
	<property name="fixedDepositDao" ref="dao" />
</bean> 
```
팩토리 클래스(인스턴스 팩토리 메서드가 들어 있는 클래스)를 일반 스프링 빈으로 정의하고, 인스턴스 팩토리 메서드에 대한 자세한 내용을 사용하는 빈은 따로 정의한다.</br>
인스턴스 팩토리 메서드의 자세한 사항을 정의하기 위해 bean 엘리먼트의 factory-bean과 factory-method 속성을 사용한다.
- factory-bean : 인스턴스 팩토리 메서드가 들어 있는 빈의 이름
- factory-method : 인스턴스 팩토리 메서드의 이름

### 팩토리 메서드로 만들어진 빈의 의존 관계 주입하기

의존관계를 팩토리 메서드에 인수로 넘기거나 세터 기반의 DI를 사용해 주입할 수 있다.

#### databaseInfo 프로퍼티를 정의하는 FixedDepositJdbcDao 클래스
```Java
public classs FixedDepositJdbcDao {
	private DatabaseInfo databaseInfo;
	...
	public FixedDepositJdbcDao() {}

	public void setDatabaseInfo(DatabaseInfo databaseInfo) {
		this.databaseInfo = databaseInfo;
	}
	...
}
```
#### FixedDepositDaoFactory
```Java
public class FixedDepositDaoFactory {
	public FixedDepositDaoFactory() {}

	public static FixedDepositDao getFixedDepositDao(String daoType, ...) {
		FixedDepositDao fixedDepositDao = null;

		if("jdbc".equalsIgnoreCase(daoType)) {
			fixedDepositDao = new FixedDepositJdbcDao();
		}
		if("hibernate".equalsIgnoreCase(daoType)) {
			fixedDepositDao = new FixedDepositHibernateDao();
		}
		.....
		return fixedDepositDao;
	}
}
```
팩토리 클래스는 기존과 동일하다.
```XML
<bean id="daoFactory" class="sample.spring.chapter02.bankapp.FixedDepositDaoFactory"/>

<bean id="dao" factory-bean="daoFactory" factory-method="getFixedDepositDao">
	<constructor-arg index="0" value="jdbc"/>
	<property name="databaseInfo" ref="databaseInfo"/>
</bean>

<bean id="databaseInfo" class="DatabaseInfo"/>
```
빈 정의에서 property 엘리먼트를 사용해 인스턴스 팩토리 메서드가 반환한 FixedDepositJdbcDao 인스턴스의 databaseInfo 프로퍼티를 설정한다.

## 생성자 기반 DI

생성자 기반 DI는 빈의 의존 관계를 빈 클래스 생성자의 인수로 설정한다.</br>
생성자 기반 DI와 세터 기반 DI는 함께 조합해서 사용할 수 있고 둘 중 하나만 사용할 수 있다.</br>
- property 엘리먼트 : 세터 기반 DI에 사용
- constructor-arg 엘리먼트 : 생성자 기반 DI에 사용

## 설정 정보를 빈에 전달하기
### property를 이용한 방법
```Java
public class EmailMessageSender {
	private String host;
	private String username;
	private String password;

	public void setHost(String host) {
		this.host = host;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	.....
}
```
```Xml
	<bean id="emailMessageSender" class="EmailMessageSender">
		<property name="host" value="smtp.gmail.com"/>
		<property name="username" value="myusername"/>
		<property name="password" value="mypassword"/>
	</bean>
```
value 속성은 name 속성이 정한 이름에 맞는 빈 프로퍼티의 String 값을 지정한다.</br>
### 생성자 인수로 설정 정보를 받는 방법
```Java
public class EmailMessageSender {
	private String host;
	private String username;
	private String password;

	public EmailMessageSender(String host, String username, String password) {
		this.host = host;
		this.username = username;
		this.password = password;
	}
	.....
}
```
```Xml
	<bean id="emailMessageSender" class="EmailMessageSender">
		<constructor-arg index="0" value="smtp.gmail.com"/>
		<constructor-arg index="1" value="myusername"/>
		<constructor-arg index="2" value="mypassword"/>
	</bean>
```
value 속성은 index 속성이 가리키는 생성자 인수에 설정할 String 값이다.</br>
원시 타입(int, long 등) 또는 컬렉션(List, Map 등) 또는 임의의 타입으로 프로퍼티나 생성자 인수를 설정하는 방법도 따로 존재한다.

## 빈 스코프

빈이 사용되는 범위, 빈 인스턴스의 수명(*lifetime*)을 결정한다.</br>
빈 정의에서 bean 엘리먼트의 scope 속성을 통해 정의하고 기본은 singleton이다.</br>
singleton, prototype, request, session, websocket, application과 같은 빈 스코프들이 존재한다.</br>

### 싱글턴 스코프
XML 파일에 정의된 모든 빈의 디폴트 스코프이다.</br>
스프링 컨테이너가 생성될 때 함께 생성되고 스프링 컨테이너가 파괴될 때 함께 파괴된다.</br>
스프링 컨테이너는 싱글턴 스코프 빈의 인스턴스를 단 하나만 만들고 그 빈에 의존하는 모든 빈에 유일한 인스턴스를 공유한다.

```Java
package sample.spring.chapter02.bankapp;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotSame;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sample.spring.chapter02.bankapp.controller.FixedDepositController;
import sample.spring.chapter02.bankapp.dao.FixedDepositDao;

public class SingletonTest {
	private static ApplicationContext context;

	@BeforeClass
	public static void init() {
		context = new ClassPathXmlApplicationContext(
				"classpath:META-INF/spring/applicationContext.xml");
	}
	// 전역 ApplicationContext를 설정한다. 
	@Test // 성공
	public void testInstances() {
		FixedDepositController controller1 = (FixedDepositController) context
				.getBean("controller");
		FixedDepositController controller2 = (FixedDepositController) context
				.getBean("controller");

		assertSame("Different FixedDepositController instances", controller1,
				controller2);
	}
	/* getBean 메서드를 통해 여러 번 요청해도 같은 빈을 반환한다. */
	@Test // 성공
	public void testReference() {
		FixedDepositController controller = (FixedDepositController) context
				.getBean("controller");
		FixedDepositDao fixedDepositDao1 = controller.getFixedDepositService()
				.getFixedDepositDao();
		FixedDepositDao fixedDepositDao2 = (FixedDepositDao) context
				.getBean("dao");

		assertSame("Different FixedDepositDao instances", fixedDepositDao1,
				fixedDepositDao2);
	}
	/* 컨트롤러 빈이 참조하는 서비스가 참조하는 Dao 인스턴스와 스프링 컨테이너의 getBean 메서드를 통해 얻은 인스턴스가 같음을 보여준다. */
	@Test // 성공
	public void testSingletonScope() {
		ApplicationContext anotherContext = new ClassPathXmlApplicationContext(
				"classpath:META-INF/spring/applicationContext.xml");
		FixedDepositController fixedDepositController1 = (FixedDepositController) anotherContext
				.getBean("controller");

		FixedDepositController fixedDepositController2 = (FixedDepositController) context
				.getBean("controller");

		assertNotSame("Same FixedDepositController instances",
				fixedDepositController1, fixedDepositController2);
	}
	/* 또다른 스프링 컨테이너의 인스턴스를 생성해서 각각의 스프링 컨테이너에서 얻은 컨트롤러 빈이 다름을 보여준다.
	싱글턴 스코프 빈 인스턴스의 존재 범위는 한 스프링 컨테이너 인스턴스 내부로 제한되기 때문이다. */
	@Test // 성공
	public void testSingletonScopePerBeanDef() {
		FixedDepositDao fixedDepositDao1 = (FixedDepositDao) context
				.getBean("dao");
		FixedDepositDao fixedDepositDao2 = (FixedDepositDao) context
				.getBean("anotherDao");

		assertNotSame("Same FixedDepositDao instances", fixedDepositDao1,
				fixedDepositDao2);
	}
	/* 빈 정의에 같은 클래스를 가리키는 두 개의 빈을 정의했다.
	스프링 컨테이너는 두 빈 정의를 별개로 처리하여 빈 정의마다 인스턴스가 각각 하나씩 생기기 때문에
	두 빈 인스턴스가 서로 다름을 보여준다. */
}
```
싱글턴 스코프 빈은 기본적으로 사전-인스턴스화 된다.</br>
즉 스프링 컨테이너가 인스턴스를 생성할 때 싱글턴 스코프 빈의 인스턴스도 생성된다.

### 싱글턴 스코프 빈을 지연 생성하기
```Xml
<bean id="lazyBean" class="example.LazyBean" lazy-init="true/>
```
bean 엘리먼트의 lazy-init 속성은 빈 인스턴스를 지연 생성할지 미리 생성할지 지정한다.</br>
이 값이 true라면 스프링 컨테이너는 빈을 처음 요청받은 즉시 인스턴스를 초기화한다.
![KakaoTalk_20220417_164050835](https://user-images.githubusercontent.com/25950908/163705458-13eb34ba-cfc2-400d-929f-bae9189ed75b.jpg)

### 프로토타입 스코프
- 스프링 컨테이너가 항상 프로토타입 스코프 빈의 새로운 인스턴스를 반환한다.
- 항상 지연 초기화된다.

빈 설정에서 bean 엘리먼트의 scope 속성을 prototype으로 설정한다.
```Java
package sample.spring.chapter02.bankapp;

import static org.junit.Assert.assertNotSame;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sample.spring.chapter02.bankapp.domain.FixedDepositDetails;

public class PrototypeTest {
	private static ApplicationContext context;

	@BeforeClass
	public static void init() {
		context = new ClassPathXmlApplicationContext(
				"classpath:META-INF/spring/applicationContext.xml");
	}

	@Test // 성공
	public void testInstances() {
		FixedDepositDetails fixedDepositDetails1 = (FixedDepositDetails) context
				.getBean("fixedDepositDetails");
		FixedDepositDetails fixedDepositDetails2 = (FixedDepositDetails) context
				.getBean("fixedDepositDetails");

		assertNotSame("Same FixedDepositDetails instances",
				fixedDepositDetails1, fixedDepositDetails2);
	}
	/* prototype scope로 생성한 두 인스턴스가 서로 다른 것을 보여준다. */
}
```

## 빈에 적합한 스코프 선택하기

빈이 어떤 대화적 상태도 유지하지 않는다면(상태가 없는 stateless 빈이라면) 싱글턴 스코프 빈으로 정의해야 하고,</br>
빈에 대화적 상태를 유지해야 한다면 프로토타입 스코프 빈으로 정의해야 한다.

> ORM 프레임워크를 사용하는 애플리케이션은 ORM 프레임워크가 도메인 객체를 생성하거나 직접 애플리케이션 코드에서 new 등을 사용해 도메인 객체를 생성한다.</br>
애플리케이션이 영속화를 위해 ORM을 사용하는 경우 XML 파일 안에 도메인 객체를 정의하지 않는다.[영속성](https://wckhg89.tistory.com/10)
