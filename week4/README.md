# 스프링 웹 MVC 기초
스프링 웹 MVC는 웹 레이어를 이루는 애플리케이션 객체 사이의 관심사를 명확하게 분리할 수 있는 **비 침투적** 프레임워크이다.<br/>
> 침투적이란?<br/>
침투적(invasive)이라는 것은 특정 기술을 적용하려면 그 기술에서 요구하는 데로 뭔가를 해 줘야 하는 경우, 예를 들어 반드시 어떤 함수를 override해야 하는 경우 침투적이다.

## Hello World 애플리케이션 이해하기
### Contoroller 클래스 
```Java
public class HelloWorldController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> modelData = new HashMap<>();
        modelData.put("msg", "Hello World !!");
        return new ModelAndView("helloworld", modelData);
    }
}
```
스프링 웹 MVC 프레임워크는 들어오는 HTTP 요청을 가로채 컨트롤러의 handleRequest 메서드를 호출한다.<br/>
handleRequest 메서드는 모델 데이터와 뷰 정보가 들어있는 ModelAndView 객체를 반환한다.<br/>
스프링 웹 MVC 프레임워크는 이후 helloworld 페이지로 HTTP 요청을 보내면서(dispatch라고 한다.) helloworld 페이지가 요청 속성으로 모델 속성을 사용할 수 있게 한다.<br/>
![image](https://user-images.githubusercontent.com/25950908/168039073-562e3525-9d14-4ef0-92a5-1e5ffb1a2902.png)
![KakaoTalk_20220512_183428542](https://user-images.githubusercontent.com/25950908/168040604-a2c097cc-8053-4057-9f78-084872b805e5.jpg)

스프링 웹 MVC를 XML로 사용할 경우, HandlerMapping 인터페이스를 구현한 SimpleUrlHandlerMapping과 ViewResolver 인터페이스를 구현한 InternalResourceViewResolver를 빈 정의에 추가해야 한다.<br/>

HandlerMapping은 들어오는 HTTP 요청을 처리할 책임이 있는 컨트롤러에 전달한다.<br/>
urlMap 프로퍼티가 URL과 컨트롤러 빈 사이의 매핑을 설정한다.

ViewResolver는 ModelAndView에 들어있는 뷰 이름으로 실제 뷰의 위치를 찾는다.<br/>
실제 뷰 위치는 prefix 프로퍼티값을 앞에 붙이고, suffix 프로퍼티값을 뒤에 붙이는 방식으로 정해진다.<br/>
ex ) /WEB-INF/jsp/ + helloworld + .jsp -> /WEB-INF/jsp/helloworld.jsp

![KakaoTalk_20220512_184807271](https://user-images.githubusercontent.com/25950908/168043176-f50ccd7f-99a2-4713-8502-30f2b64cc3e1.jpg)

### web.xml - 웹 애플리케이션 배포 디스크립터
```Xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<web-app xmlns="java.sun.com/xml/ns/javaee" 
	xmlns:xsi="w3.org/2001/XMLSchema-instance" 
	xsi:schemaLocation="java.sun.com/xml/ns/javaee java.sun.com/xml/ns/javaee/web-app_3_0.xsd" 
	version="3.0">

	<servlet>
		<servlet-name>hello</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet
		</servlet-class>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>/WEB-INF/spring/myapp-config.xml</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>

	<servlet-mapping>
		<servlet-name>hello</servlet-name>
		<url-pattern>/helloworld/*</url-pattern>
	</servlet-mapping>
</web-app>
```
web.xml파일에 DispatcherServlet 설정이 존재하고, contextConfigLocation 초기화 파라미터는 myapp-config.xml 파일을 가리킨다.<br/>
```Xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

	<bean name="helloWorldController" class="sample.spring.chapter12.web.HelloWorldController" />

	<bean id="urlHandler"
		class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
		<property name="urlMap">
			<map>
				<entry key="/sayhello" value-ref="helloWorldController" />
			</map>
		</property>
	</bean>

	<bean id="viewResolver"
		class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="prefix" value="/WEB-INF/jsp/" />
		<property name="suffix" value=".jsp" />
	</bean>
</beans>
```

스프링 웹 MVC 기반 애플리케이션에서는 요청을 스프링 웹 MVC가 제공하는 서블릿인 DispatcherServlet이 가로챈다.<br/>
DispatcherServlet은 요청을 적절한 컨트롤러에 전달하는 역할을 한다.<br/>
DispatcherServlet은 웹 애플리케이션 컨텍스트 XML에 정의된 HandlerMapping과 ViewResolver 빈을 요청 처리에 사용한다.<br/>
HandlerMapping 구현을 사용해 요청에 맞는 적절한 컨트롤러를 찾고 ViewResolver 구현을 사용해 컨트롤러가 반환하는 뷰 이름을 가지고 실제 뷰를 찾는다.<br/>
![KakaoTalk_20220512_190103357](https://user-images.githubusercontent.com/25950908/168045729-65c8d900-0fb9-4a27-b914-6446a28c4c30.jpg)

**http://localhost:8080/ch12-helloworld/helloworld/sayhello**<br/>
위 URL의 /ch12-helloworld 부분은 웹 어플리케이션의 컨텍스트 경로이고, /helloworld 부분은 DispatcherServlet에 매핑되고, /sayhello 부분은 컨트롤러에 매핑된다.

## DispathcerServlet - 프론트 컨트롤러
초기화 시 DispathcerServlet은 자신에게 대응하는 웹 애플리케이션 컨텍스트 XML 파일을 로드하고 스프링 WebApplicationContext 객체 인스턴스를 만든다.<br/>
WebApplicationContext 는 웹에 특화된 기능을 제공하는 ApplicationContext 인터페이스의 하위 인터페이스이다. (빈에 request, session등의 추가 스코프가 존재)<br/>
여러 모듈로 나뉜 웹 애플리케이션에서는 web.xml 파일에서 각 모듈별로 DispatcherServlet을 정의할 수도 있다.<br/>
각 DispatcherServlet에는 해당 모듈에만 해당하는 빈을 포함하는 자신만의 웹 애플리케이션 컨텍스트 XML 파일이 있다.<br/>
여러 DispatcherServlet 사이에 공유되는 빈은 루트 웹 애플리케이션 컨텍스트 XML 파일에 정의된 것들이다.
![KakaoTalk_20220515_205041406](https://user-images.githubusercontent.com/25950908/168471340-99de9c7c-860b-464f-8650-903d5dbcf27d.jpg)
