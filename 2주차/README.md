# 2주차. 빈 설정
## 빈 정의 상속
![KakaoTalk_20220424_150739430](https://user-images.githubusercontent.com/25950908/164959116-7f013c07-f7f2-4edf-8a02-60c7c37a940e.jpg)
모든 DAO가 DatabaseOperations에 의존해 데이터베이스 연산을 수행한다.

#### DatabaseOperations 빈에 의존하는 DAO 빈
```Xml
<bean id="databaseOperations" class="sample.spring.chapter03.bankapp.utils.DatabaseOperations" />

<bean id="personalBankingDao" class="sample.spring.chapter03.bankapp.dao.PersonalBankingDaoImpl">
    <property name="databaseOperations" ref="databaseOperations"/>
</bean>

<bean id="fixedDepositDao" class="sample.spring.chapter03.bankapp.dao.FixedDepositDaoImpl">
    <property name="databaseOperations" ref="databaseOperations"/>
</bean>
```
애플리케이션의 여러 빈이 같은 설정 집합(프로퍼티, 생성자 인수 등)을 공유한다면, 다른 빈 정의의 부모 역할을 하는 빈 정의를 만들 수 있다.</br>
빈 정의의 상속을 통해 위의 예제의 공통 설정인 databaseOperations 프로퍼티를 공유할 수 있다.
```Xml
<bean id="databaseOperations" class="sample.spring.chapter03.bankapp.utils.DatabaseOperations" />

<bean id="daoTemplate" abstract="true">
    <property name="databaseOperations" ref="databaseOperations" />
</bean>

<bean id="personalBankingDao" parent="daoTemplate" 
    class="sample.spring.chapter03.bankapp.dao.PersonalBankingDaoImpl" />

<bean id="fixedDepositDao" parent="daoTemplate" 
    class="sample.spring.chapter03.bankapp.dao.FixedDepositDaoImpl" />
```
- daoTemplate 빈 정의는 두 빈 정의가 공유하는 공통 설정을 정의한다.</br>property 엘리먼트를 사용해 공통적으로 필요한 databaseOperations 의존 관계를 정의한다.
- bean 엘리먼트의 parent 속성은 설정을 상속할 빈 정의의 이름을 지정한다.</br>
여기서는 두 dao 빈의 parent 속성이 daoTemplate 이므로 daoTemplate의 databaseOperations 프로퍼티를 상속한다.
- bean 엘리먼트의 abstract 속성을 `true`로 만들면 그 빈이 추상 빈이라는 뜻이다.</br>
스프링 컨테이너는 추상 빈 정의에 해당하는 빈을 생성하지 않는다.</br>
추상 빈에 의존하는 빈을 정의할 수 없다. -> 추상 빈을 참조하는 property나 constructor-arg 엘리먼트를 사용할 수 없다.
- daoTemplate 빈 정의는 class 속성을 지정하지 않았다. 부모 빈 정의가 class 속성을 지정하지 않으면 자식 빈 정의가 class 속성을 정의한다. class 속성을 지정하지 않는 빈은 꼭 추상 빈으로 만들어야 스프링 컨테이너가 그 빈 인스턴스를 생성하지 않는다.
![KakaoTalk_20220424_153215391](https://user-images.githubusercontent.com/25950908/164959927-9a7246f4-28e5-49eb-85be-d6ad44de3d0c.jpg)
부모 빈의 property가 상속되는 것, 추상 빈을 생성하지 않는 것을 보여준다.
### 빈 정의로 상속할 수 있는 정보
- 프로퍼티 : property 엘리먼트로 설정
- 생성자 인수 : constructor-arg 엘리먼트로 설정
- 메서드 오버라이드
- 초기화와 정리 메서드
- 팩토리 메서드 : bean 엘리먼트의 factory-method 속성으로 설정
#### 부모 빈 정의가 추상이 아닌 경우
```Xml
<bean id="serviceTemplate"
        class="sample.spring.chapter03.bankapp.base.ServiceTemplate">
    <property name="jmsMessageSender" ref="jmsMessageSender" />
    <property name="emailMessageSender" ref="emailMessageSender" />
    <property name="webServiceInvoker" ref="webServiceInvoker" />
</bean>

<bean id="fixedDepositService"
    class="sample.spring.chapter03.bankapp.service.FixedDepositServiceImpl"
    parent="serviceTemplate">
    <property name="fixedDepositDao" ref="fixedDepositDao" />
</bean>

<bean id="personalBankingService"
    class="sample.spring.chapter03.bankapp.service.PersonalBankingServiceImpl"
    parent="serviceTemplate">
    <property name="personalBankingDao" ref="personalBankingDao" />
</bean>

<bean id="userRequestController"
    class="sample.spring.chapter03.bankapp.controller.UserRequestControllerImpl">
    <property name="serviceTemplate" ref="serviceTemplate" />
</bean>
```
추상 빈 정의가 아닌 빈 정의를 상속한다.</br>
부모 빈 정의의 프로퍼티를 자식 빈 정의가 상속하기 때문에 자식 구현 클래스는 부모의 property들에 대한 세터 메서드를 반드시 정의해야 한다.</br>
세터 메서드를 자식 구현 클래스에 정의할 수도 있고, 자식 구현 클래스를 부모 클래스인 ServiceTemplate 클래스의 하위 클래스로 만들 수도 있다.</br>
serviceTemplate 빈 정의가 추상이 아니기 때문에 userRequestController 빈이 자신의 의존 관계에 ServiceTemplate 빈을 지정할 수 있다.
```Java
package sample.spring.chapter03.bankapp.service;

import sample.spring.chapter03.bankapp.base.ServiceTemplate;
import sample.spring.chapter03.bankapp.dao.PersonalBakingDao;
import sample.spring.chapter03.bankapp.domain.BankStatement;

public class PersonalBankingServiceImpl extends ServiceTemplate implements PersonalBankingService { // ServiceTemplate 클래스를 상속받음

	private PersonalBakingDao personalBakingDao;

	public void setPersonalBankingDao(PersonalBakingDao personalBakingDao) { // 해당 빈 정의의 property에 대한 세터 메서드 구현
		this.personalBakingDao = personalBakingDao;
	}

	@Override
	public BankStatement getMiniStatement() {
		return personalBakingDao.getMiniStatement();
	}
}
```
![KakaoTalk_20220424_155456578](https://user-images.githubusercontent.com/25950908/164960732-e973966c-b357-4f8f-9ce2-7cc44390b680.jpg)
- 부모 빈 정의가 추상이 아니어도 된다.
- 자식 빈 정의에서 프로퍼티를 추가 정의 할 수 있다.
- 부모 빈 정의가 참조하는 클래스와 자식 빈 정의가 참조하는 클래스 사이에서 상속 관계가 존재할 수 있다.
- 부모 빈 정의를 자식 빈 뿐만 아니라 XML 파일에 있는 다른 빈도 사용할 수 있다.

#### 팩토리 메서드 설정 상속
```Java
package sample.spring.chapter03.bankapp.controller;

public class ControllerFactory {
	public Object getController(String controllerName) {
		Object controller = null;
		if ("fixedDepositController".equalsIgnoreCase(controllerName)) {
			controller = new FixedDepositControllerImpl();
		}
		if ("personalBankingController".equalsIgnoreCase(controllerName)) {
			controller = new PersonalBankingControllerImpl();
		}
		return controller;
	}
}
```
```Xml
	<bean id="controllerFactory"
		class="sample.spring.chapter03.bankapp.controller.ControllerFactory" />

	<bean id="controllerTemplate" factory-bean="controllerFactory"
		factory-method="getController" abstract="true">
	</bean>

	<bean id="fixedDepositController" parent="controllerTemplate">
		<constructor-arg index="0" value="fixedDepositController" />
		<property name="fixedDepositService" ref="fixedDepositService" />
	</bean>

	<bean id="personalBankingController" parent="controllerTemplate">
		<constructor-arg index="0" value="personalBankingController" />
		<property name="personalBankingService" ref="personalBankingService" />
	</bean>
```
자식 빈이 getController 인스턴스 팩토리 메서드 설정을 부모 빈 정의로부터 상속한다.</br>
controllerTemplate 빈 정의는 ControllerFactory의 getController 팩토리 메서드를 사용해 빈 인스턴스를 생성하라고 지정한다.</br>
controllerTemplate 빈 정의가 추상 빈이기 때문에 getController 팩토리 메서드 설정을 사용할지 여부를 결정하는 것은 자식 빈 정의다.</br>
자식 빈 정의는 constructor-arg 엘리먼트를 사용해 팩토리 메서드에 인수를 전달한다.</br>

## 생성자 인수 매치하기
생성자 인수가 간단한 자바 타입(int, String 등) 이라면 constructor-arg 엘리먼트의 value 속성을 사용해 생성자 인수값을 지정한다.</br>
생성자 인수가 빈에 대한 참조라면 constructor-arg의 ref 속성을 사용해 빈 이름을 지정한다.</br>

constructor-arg 엘리먼트의 index 속성을 지정하지 않으면 스프링 컨테이너는 constructor-arg 엘리먼트에 의해 참조되는 타입을 빈 클래스 생성자의 인수 타입과 매치시켜서 어떤 생성자 인수를 호출할지 결정한다.

```Java
package sample.spring.chapter03.bankapp.base;

public class ServiceTemplate {
	private JmsMessageSender jmsMessageSender;
	private EmailMessageSender emailMessageSender;
	private WebServiceInvoker webServiceInvoker;

	public ServiceTemplate(JmsMessageSender jmsMessageSender,
			EmailMessageSender emailMessageSender,
			WebServiceInvoker webServiceInvoker) {
		this.jmsMessageSender = jmsMessageSender;
		this.emailMessageSender = emailMessageSender;
		this.webServiceInvoker = webServiceInvoker;
    }
}
```
```Xml
<bean id="serviceTemplate"
    class="sample.spring.chapter03.bankapp.base.ServiceTemplate">
    <constructor-arg ref="emailMessageSender" />
    <constructor-arg ref="jmsMessageSender" />
    <constructor-arg ref="webServiceInvoker" />
</bean>
```
호출할 생성자 인수의 타입이 명확히 다른 경우(각 타입 간에 서로 상속 관계가 없는 경우)</br>
클래스의 생성자에 지정된 순서와 빈 정의에 정의한 순서가 달라도 타입을 모두 구분할 수 있으므로 생성자에 올바른 순서로 각 빈을 주입할 수 있다.</br>

서로 상속 관계인 스프링 빈들을 생성자 인수로 사용할 경우
```Java
public class SampleBean {
    public SampleBean(ABean aBean, BBean bBean) {
        .....
    }
    .....
}
```
```Xml
<bean id="aBean" class="example.ABean"/>
<bean id="bBean" class="example.BBean"/>

<bean id="sampleBean" class="example.SampleBean">
    <constructor-arg ref="bBean"/>
    <constructor-arg ref="aBean"/>
</bean>
```
BBean이 ABean의 하위 클래스일 때</br>
sampleBean의 빈 정의에서 첫 번째 constructor-arg 엘리먼트는 bBean 빈을, 두 번째 constructor-arg 엘리먼트는 aBean 빈을 가리킨다.</br>
따라서 SampleBean의 생성자를 호출할 때 bBean, aBean 순서로 전달된다.</br>
ABean(상위 클래스)의 인스턴스를 BBean(하위 클래스)의 인스턴스가 필요한 곳에 넘길 수 없기 때문에 예외가 발생한다.</br>
이를 막기 위해 index나 type 속성을 지정해서 어떤 생성자 인수로 적용할 지 구별해야 한다.</br>
type 속성 : constructor-arg 엘리먼트를 적용할 타입의 전체 이름을 지정</br>
index 속성 : constructor-arg 엘리먼트를 적용할 순서를 지정
둘 이상의 생성자 인수가 같은 타입이라면 index 속성을 사용해야 모호성을 없앨 수 있다.

### 표준 자바 타입과 사용자 지정 타입을 생성자 인수로 사용하기
생성자 인수가 원시 타입, String 타입, 사용자 지정 타입인 경우 value 속성을 사용해 인수값을 지정할 수 있다.</br>
value 속성에 지정된 문자열을 가지고 변화할 수 있는 생성자 인수가 2개 이상이라면 스프링 컨테이너가 문자열을 변환할 생성자 인수 타입을 알아낼 수 없다. -> type 속성을 사용해 생성자 인수 타입을 명확히 지정해야 한다.

```Java
package sample.spring.chapter03.bankapp.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class TransferFundsServiceImpl implements TransferFundsService {
	private static Logger logger = LogManager
			.getLogger(TransferFundsServiceImpl.class);

	private String webServiceUrl;
	private boolean active;
	private long timeout;
	private int numberOfRetrialAttempts;

	public TransferFundsServiceImpl(String webServiceUrl, boolean active, long timeout,
			int numberOfRetrialAttempts) {
		this.webServiceUrl = webServiceUrl;
		this.active = active;
		this.timeout = timeout;
		this.numberOfRetrialAttempts = numberOfRetrialAttempts;

		logger.info("Web Service URL: " + webServiceUrl + ", active : " + active + ", timeout : "
				+ timeout + ", numberOfRetrialAttempts "
				+ numberOfRetrialAttempts);
	}

	public void transferFunds() {

	}
}
```
```Xml
<bean id="transferFundsService"
    class="sample.spring.chapter03.bankapp.service.TransferFundsServiceImpl">
    <constructor-arg type="java.lang.String" value="http://someUrl.com/xyz" />
    <constructor-arg type="boolean" value="true" />
    <constructor-arg type="int" value="5" />
    <constructor-arg type="long" value="200" />
</bean>
```
type을 지정하지 않으면 constructor-arg 엘리먼트 순서대로 전달 된다.</br>
같은 타입이 여러개라면, index 속성을 사용해 지정하는 수밖에 없다.
### 이름으로 생성자 인수 매치시키기
constructor-arg의 name 속성을 사용하면 생성자 인수의 이름을 지정할 수 있다.
```Xml
<bean id="transferFundsService"
    class="sample.spring.chapter03.bankapp.service.TransferFundsServiceImpl">
    <constructor-arg name="webServiceUrl" value="http://someUrl.com/xyz" />
    <constructor-arg name="active" value="true" />
    <constructor-arg name="numberOfRetrialAttempts" value="5" />
    <constructor-arg name="timeout" value="200" />
</bean>
```
변수 이름을 통해 구별할 수 있는데, 컴파일 할때 디버그 플래그나 파라미터 이름 발견 플래그를 사용한 경우에만 쓸 수 있다.</br>
.class 파일에 파라미터 이름이 저장되는 식

이를 원하지 않으면, @ConstructorProperties 에너테이션을 사용해 생성자 인수 이름을 명시한다.

```Java
package sample.spring.chapter03.bankapp.service;

import java.beans.ConstructorProperties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class TransferFundsServiceImpl implements TransferFundsService {
	private static Logger logger = LogManager.getLogger(TransferFundsServiceImpl.class);

	private String webServiceUrl;
	private boolean active;
	private long timeout;
	private int numberOfRetrialAttempts;
    // 애너테이션을 통해 이름 전달
	@ConstructorProperties({ "webServiceUrl", "active", "timeout", "numberOfRetrialAttempts" }) 
	public TransferFundsServiceImpl(String webServiceUrl, boolean active, long timeout, int numberOfRetrialAttempts) {
		this.webServiceUrl = webServiceUrl;
		this.active = active;
		this.timeout = timeout;
		this.numberOfRetrialAttempts = numberOfRetrialAttempts;

		logger.info("Web Service URL: " + webServiceUrl + ", active : " + active + ", timeout : " + timeout
				+ ", numberOfRetrialAttempts " + numberOfRetrialAttempts);
	}

	public void transferFunds() {

	}
}
```
constructor-arg 엘리먼트와 @ConstructorProperties에서 같은 이름을 사용해야 한다.</br>
부모 빈 정의에 해당하는 클래스 생성자에 @ConstructorProperties가 있으면 자식 빈에 해당하는 빈 클래스 생성자에도 반드시 @ConstructorProperties가 있어야 한다.</br>
@ConstructorProperties는 생성자만을 위한 것이므로 팩토리 메서드에 @ConstructorProperties를 설정할 수 없다.

## 다른 타입의 빈 프로퍼티와 생성자 인수 설정하기
스프링 PropertyEditor : 다양한 타입을 빈 프로퍼티나 생성자 인수로 쉽게 넘길 수 있도록 지원한다.</br>
### 스프링 내장 프로퍼티 에디터
자바빈의 PropertyEditor는 자바 타입을 문자열값으로 바꾸거나 역방향으로 바꾸기 위해 필요한 로직을 제공한다.</br>
스프링은 프로퍼티나 생성자의 실제 자바 타입과 빈 프로퍼티나 생성자 인수의 문자열 값을 상호 변환해주는 몇 가지 내장 PropertyEditor를 제공한다.
```Java
package sample.spring.chapter03.beans;

import java.util.Currency;
import java.util.Date;
import java.util.Properties;

public class BankDetails {
	private String bankName;
	private byte[] bankPrimaryBusiness;
	private char[] headOfficeAddress;
	private char privateBank;
	private Currency primaryCurrency;
	private Date dateOfInception;
	private Properties branchAddresses;

	public String getBankName() {
		return bankName;
	}
    ..... //getter, setter들
}
```
```Xml
<bean id="bankDetails" class="sample.spring.chapter03.beans.BankDetails">
    <property name="bankName" value="My Personal Bank" />
    <property name="bankPrimaryBusiness" value="Retail banking" />
    <property name="headOfficeAddress" value="Address of head office" />
    <property name="privateBank" value="Y" />
    <property name="primaryCurrency" value="INR" />
    <property name="dateOfInception" value="30-01-2012"></property>
    <property name="branchAddresses">
        <value>
            x = Branch X's address
            y = Branch Y's address
        </value>
    </property>
</bean>
```
여러 타입의 문자열 값을 지정하면, 스프링 컨테이너는 등록된 PropertyEditor를 사용해 프로퍼티나 생성자 인수의 문자열 값을 그에 상응하는 자바 타입으로 변환한다.</br>
값이 여러 줄이라면 value 값을 지정하는 것보다 value 하위 엘리먼트를 더 선호한다.
![KakaoTalk_20220424_173126736](https://user-images.githubusercontent.com/25950908/164967656-ee46907a-f784-4632-bf82-fb825d17dc95.jpg)

스프링의 모든 내장 PropertyEditor가 스프링 컨테이너에 디폴트로 등록되진 않는다. 예를 들어 문자열 -> java.util.Date 타입으로 변환
### 컬렉션 타입에 값 지정하기
property나 constructor-arg 엘리먼트의 list, map, set 하위 엘리먼트들을 사용해서 컬렉션 타입의 프로퍼티나 생성자 인수를 설정할 수 있다.
```Xml
<bean id="dataTypes" class="sample.spring.chapter03.beans.DataTypesExample">
    .....
    <constructor-arg name="anotherPropertiesType">
        <props>
            <prop key="book">Getting started with the Spring Framework</prop>
        </props>
    </constructor-arg>
    <constructor-arg name="anotherPropertiesType">
        <props>
            <prop key="book">Getting started with the Spring Framework</prop>
        </props>
    </constructor-arg>
    <constructor-arg name="listType">
        <list>
            <value >1</value>
            <value>2</value>
        </list>
    </constructor-arg>
    <constructor-arg name="mapType">
        <map>
            <entry>
                <key>
                    <value>map key 1</value>
                </key>
                <value>map key 1’s value</value>
            </entry>
        </map>
    </constructor-arg>
    <constructor-arg name="setType">
        <set>
            <value>Element 1</value>
            <value>Element 2</value>
        </set>
    </constructor-arg>
</bean>
```
빈 프로퍼티나 생성자 인수의 타입이 List\<List\>면 다음과 같이 list 엘리먼트를 내포시켜 설정할 수 있다.
```Xml
<constructor-arg name="nestedList">
    <list>
        <list>
            <value>String value 1</value>
            <value>String value 2</value>
        </list>
    </list>
</constructor-arg>
```
이런 식으로 컬렉션 안에 컬렉션을 내포할 수도 있고, 빈 참조를 내포할 수도 있다.
```Xml
<bean .....>
    <constructor-arg name="myList">
        <list>
            <ref bean="aBean" />
            <ref bean="bBean" />
        </list>
    </constructor-arg>
</bean>
<bean id="aBean" class="example.ABean" />
<bean id="bBean" class="example.bBean" />

빈 이름(bean 엘리먼트의 id 속성)을 List, Map, Set 타입의 생성자 인수나 빈 프로퍼티에 추가하려면 idref 엘리먼트를 map, set, list 엘리먼트 안에 사용해야 한다.
``` Xml
<constructor-arg name="myExample">
    <map>
        <entry>
            <key>
                <idref bean="sampleBean" />
            </key>
            <ref bean="sampleBean" />
        </entry>
    </map>
</constructor-arg>
```
value 엘리먼트를 사용할 수도 있겠지만, idref 엘리먼트를 사용하면 애플리케이션이 실행될 떄 스프링 컨테이너가 sampleBean이라는 이름의 빈이 있는지 *검증*할 수 있다.

> null 엘리먼트를 사용해 null값을 컬렉션에 추가할 수 있다.

> property 또는 constructor-arg 엘리먼트 내부에 array 하위 엘리먼트를 사용해 배열 타입의 프로퍼티 값을 설정할 수 있다.

### list, set, map 엘리먼트의 디폴트 구현
- list : java.util.ArrayList
- set : java.util.LinkedHashSet
- map : java.util.LinkedHashMap
디폴트 구현이 아닌 다른 구현을 사용할 경우에도 (ex: java.util.LinkedList) list, set, map 엘리먼트를 사용할 것을 권장한다.</br>
빈의 생성자 인수나 프로퍼티에 대입하려는 구체적인 컬렉션 클래스의 완전한 이름을 지정하기 위한 옵션을 제공한다.

## 내장 프로퍼티 에디터
### CustomCollectionEditor
원본 컬렉션 타입(ex : LinkedList)을 대상 컬렉션 타입(ex : ArrayList)으로 변환할 때 쓰인다.</br>
기본적으로 Set, SortedSet, List타입에 대해 등록되어 있다.
```Java
public class CollectionTypesExample {
	private List listType;
	private Set setType;
	private Map mapType;
    .....
}
```
```Xml
<bean class="sample.spring.chapter03.beans.CollectionTypesExample">
    <property name="listType">
        <set>
            <value>set element 1</value>
            <value>set element 2</value>
        </set>
    </property>
    <property name="setType">
        <list>
            <value>list element 1</value>
            <value>list element 2</value>
        </list>
    </property>
    <property name="mapType">
        <map>
            <entry>
                <key>
                    <value>map key</value>
                </key>
                <value>map value</value>
            </entry>
        </map>
    </property>
</bean>
```
listType 프로퍼티를 set 엘리먼트로 지정하고, setType 프로퍼티를 list로 지정해도, CustomCollectionEditor가 프로퍼티를 설정하기 전에 각 타입의 구현으로 변환한다.
![KakaoTalk_20220424_182435292](https://user-images.githubusercontent.com/25950908/164969660-b4ff3665-6225-4cff-be8d-9109dfea740e.jpg)

1. 스프링은 set 엘리먼트에 해당하는 LinkedHashSet 인스턴스를 만든다.
2. listType 프로퍼티의 타입이 List이기 때문에, CustomCollectionEditor가 관여해서 listType 프로퍼티값을 설정한다.
3. CustomCollectionEditor는 ArrayList 인스턴스를 만들고, LinkedHashSet의 원소들을 인스턴스 내부에 집어넣는다.
4. listType 변수값을 CustomCollectionEditor가 만든 ArrayList 구현으로 설정한다.

### CustomMapEditor
원본 Map 타입을 대상 Map 타입으로 변환한다.
### CustomDateEditor
java.util.Date 타입의 프로퍼티와 생성자 인수를 위한 프로퍼티 에디터이다.</br>
Date 타입을 문자열로 형식화하거나 날짜/시간을 표현하는 문자열을 파싱해서 Date 타입의 객체를 만들 때 쓰이는 사용자 지정 java.text.DateFormat을 지원한다.</br>
CustomDateEditor는 스프링 컨테이너에 등록되지 않으므로 직접 등록해야 한다.</br>
> 스프링의 CustomEditorConfigurer 특별 빈을 사용해 프로퍼티 에디터를 스프링 컨테이너에 등록할 수 있다.</br>
CustomEditorConfigurer 클래스는 스프링의 BeanFactoryPostProcessor 인터페이스를 구현하고, 스프링 컨테이너는 자동으로 CustomEditorConfigurer를 감지해 실행시킨다.

#### 프로퍼티 에디터 등록 단계
1. 스프링의 PropertyEditorRegistrar 인터페이스를 구현한 클래스를 만든다.</br>
클래스는 스프링 컨테이너에 프로퍼티 에디터를 등록한다.
2. XML 파일에 PropertyEditorRegistrar 구현을 스프링 빈으로 등록한다.
3. XML 파일에 스프링의 CustomEditorConfigurer 특별 빈을 설정한다.</br>
이때 1, 2단계에서 만든 PropertyEditorRegistrar 구현에 대한 참조를 지정한다.

### PropertyEditorRegistrar 구현 만들기
```Java
package sample.spring.chapter03.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.CustomDateEditor;

public class MyPropertyEditorRegistrar implements PropertyEditorRegistrar {

	@Override
	public void registerCustomEditors(PropertyEditorRegistry registry) {
		registry.registerCustomEditor(Date.class, new CustomDateEditor(
				new SimpleDateFormat("dd-MM-yyyy"), false));
	}
}
```
PropertyEditorRegistry : 스프링 컨테이너에 빈 프로퍼티 에디터를 등록하는 인스턴스</br>
PropertyEditorRegistrar 인터페이스의 registerCustomEditors 메서드의 구현에 registerCustomEditor 메서드를 이용해 CustomDateEditor를 등록한다.
```Xml
<bean id="myPropertyEditorRegistrar" class="sample.spring.chapter03.beans.MyPropertyEditorRegistrar" />
<bean class="org.springframework.beans.factory.config.CustomEditorConfigurer">
    <property name="propertyEditorRegistrars">
        <list>
            <ref bean="myPropertyEditorRegistrar"/>
        </list>
    </property>
</bean>
```
위의 빈 정의는 구현 클래스를 빈으로 정의한다.</br>
CustomEditorConfigurer의 propertyEditorRegistrars 프로퍼티는 PropertyEditorRegistrar 구현 목록을 리스트로 지정한다.</br>
해당 리스트에 정의한 빈을 추가하면, 스프링 컨테이너가 CustomEditorConfigurer 빈을 자동으로 감지해 실행한다.</br>
결과적으로 MyPropertyEditorRegistrar 인스턴스에 의해 프로퍼티 에디터가 등록된다.

### p-이름공간과 c-이름공간으로 빈 정의를 간결하게 만들기
p와 c 이름공간은 property와 constructor-arg 원소를 대신한다.
