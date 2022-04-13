# 1주차. 스프링 프레임워크

`스프링 프레임워크` : 자바 엔터프라이즈 애플리케이션 개발을 단순하게 해주는 오픈소스 애플리케이션 프레임워크

스프링의 중심에는 IOC 컨테이너가 있다.</br>
IOC 컨테이너는 DI 기능을 제공한다.</br>
스프링 IoC 컨테이너를 *스프링 컨테이너* 라고도 한다.

## 스프링 IoC 컨테이너
`의존 관계` : 객체가 다른 객체와 상호작용하는 경우를 객체의 의존 관계라고 한다.</br>
X 객체가 Y, Z 객체와 상호작용한다면 X 객체는 Y, Z 객체와 의존관계이다.</br>
`DI (의존관계 주입, Dependency Injection)` : 객체 간의 의존관계를 **생성자 인수**나 **세터 메서드 인수**로 명시하고 객체를 생성할 때 생성자나 세터를 통해 의존관계를 주입하는 방식을 따르는 디자인 패턴</br>
`빈 (Bean)` : 스프링 컨테이너가 생성하고 관리하는 애플리케이션 객체들</br>
의존관계를 만들고 주입하는 책임은 애플리케이션의 객체가 아닌 스프링 컨테이너에 있어 DI를 제어의 역전(IoC)이라고도 부른다.

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
스프링 컨테이너는 자바 리플렉션 API를 사용해 객체를 만들고 의존관계를 주입한다.</br>
[리플렉션API](https://tecoble.techcourse.co.kr/post/2020-07-16-reflection-api/) : 구체적인 클래스 타입을 알지 못해도, 그 클래스의 정보에 접근할 수 있게 해주는 자바 API

스프링 컨테이너를 통해 트랜잭션 관리, 보안, 원격 접근 등 엔터프라이즈 서비스를 투명하게 객체에 적용할 수 있다.</br>
스프링 컨테이너가 애플리케이션 객체에 추가 기능을 부여하고 애플리케이션 객체를 *평범한 자바 객체*(POJO, Plane Old Java Object)로 모델링 할 수 있다.

## 스프링의 장점

1. 객체 생성과 의존관계 주입을 처리함으로써 자바 애플리케이션 조합을 쉽게 만들어준다.
2. 스프링은 POJO로 애플리케이션을 개발하는 것을 권장한다.

## 스프링 DI 기능을 사용하는 애플리케이션의 5단계

1. 애플리케이션에 쓰이는 여러 객체와 객체 간 의존관계를 파악한다.
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
3. 애플리케이션 객체 간 의존관계를 표현하는 설정 메타데이터를 만든다.

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
property 엘리먼트는 bean 엘리먼트가 설정하는 빈의 의존 관계를 지정한다. 스프링 컨테이너가 의존관계를 설정하기 위해 호출할 자바빈 스타일 세터 메서드와 대응된다.</br>
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