# 스프링 데이터

스프링은 JDBC 위에 추상 계층을 추가해 데이터베이스와 상호 작용을 편리하게 만든다.<br/>
하이버네이트나 마이바티스 등의 ORM 프레임워크로 데이터베이스 상호작용도 단순화시킨다.<br/>

> JDBC(Java Database Connectivity) : 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API<br/>

스프링 JDBC 모듈을 사용해 데이터베이스와 상호작용하는 애플리케이션 만들기
- 데이터 소스를 식별하는 javax.sql.DataSource 객체를 설정한다.
- 데이터베이스와 상호 작용하기 위해 스프링 JDBC 모듈 클래스를 사용하는 DAO를 구현한다.

## 데이터 소스 설정하기
스프링으로 독립 실행 애플리케이션을 개발할 때에는 빈으로 데이터 소스를 설정한다.<br/>
엔터프라이즈 애플리케이션을 개발한다면 애플리케이션 서버의 JNDI에 바인드된 데이터 소스를 정의할 수 있다.<br/>
스프링의 jee 스키마를 통해 JNDI에 바인드된 데이터 소스를 얻고, 이를 빈으로 등록할 수 있다.

> JNDI(Java Naming and Directory Interface) : 디렉터리 서비스에서 제공하는 데이터 및 객체를 발견(discover)하고 참고(lookup)하기 위한 자바 API이다.<br/>
데이터베이스 커넥션들을 자바 웹 응용 프로그램이 아닌 WAS에서 데이터베이스 커넥션을 생성하고 풀(Pool)로 관리한다.<br/>
자바 웹 응용 프로그램에서는 JNDI의 lookup()을 통해 리소스에 접근하고 데이터베이스 커넥션을 가져와 이용한다.

> [커넥션 풀](https://steady-coding.tistory.com/564)(Connection Pool) 데이터베이스로의 추가 요청이 필요할 때 연결을 재사용할 수 있도록 관리되는 **데이터베이스 연결의 캐시**이다.

#### applicationContext.xml
```Xml
<context:property-placeholder location="classpath*:META-INF/spring/database.properties" />

<bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close" >
    <property name="driverClassName" value="${database.driverClassName}" />
    <property name="url" value="${database.url}" />
    <property name="username" value="${database.username}" />
    <property name="password" value="${database.password}" />
</bean>
```
### database.properties
```properties
database.driverClassName=com.mysql.jdbc.Driver
database.url=jdbc\:mysql\://localhost\:3306/spring_bank_app_db?useSSL=false
database.username=root
database.password=rootpassword
```
context의 property-placeholder 엘리먼트는 database.properties 파일에서 프로퍼티를 로드하여 XML 파일의 빈 정의에 사용할 수 있도록 한다.<br/>
dataSource빈은 javax.sql.DataSource 객체이고, 데이터소스로의 연결을 생성하는 팩토리 역할을 한다.<br/>
BasicDataSource 클래스는 DataSource 인터페이스를 구현한 클래스로 연결 풀링(connection pooling) 기능을 지원한다.

## 스프링 JDBC 모듈 클래스를 사용하는 DAO 만들기
스프링 JDBC 모듈은 데이터베이스와의 상호 작용을 쉽게 만들어주는 여러 클래스를 제공한다.
### JdbcTemplacte
Connection, Statement, ResultSet 객체를 관리하고, JDBC 예외를 잡아서 더 이해하기 좋은 예외로 변환하며, 배치 연산을 수행하는 등의 일을 한다.<br/>
JdbcTemplate은 javax.sql.Datasource를 둘러싼 래퍼 역할을 한다.<br/>
JdbcTemplate 인스턴스는 보통 데이터베이스 연결을 얻을 때 사용할 DataSource 객체로 초기화 된다.
```xml
<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
    <property name="dataSource" ref="dataSource" />
</bean>
```

```Java
@Repository(value = "fixedDepositDao")
public class FixedDepositDaoImpl implements FixedDepositDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int createFixedDeposit(final FixedDepositDetails fdd) {
        final String sql = "insert into fixed_deposit_details(account_id, FD_date, amount, tenure, active) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[] { "fixed_deposit_id" });
                ps.setInt(1, fdd.getBankAccountId());
                ps.setDate(2, new java.sql.Date(fdd.getFdCreationDate().getTime()));
                ps.setInt(3, fdd.getFdAmount());
                ps.setInt(4, fdd.getTenure());
                ps.setString(5, fdd.getActive());
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public FixedDepositDetails getFixedDepsoit(final int fixedDepositId) {
        final String sql = "select * from fixed_deposit_details where fixed_deposit_id = :fixedDepositId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("fixedDepositId", fixedDepositId);
        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<FixedDepositDetails>() {
            public FixedDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                FixedDepositDetails fdd = new FixedDepositDetails();
                fdd.setActive(rs.getString("active"));
                fdd.setBankAccountId(rs.getInt("account_id"));
                fdd.setFdAmount(rs.getInt("amount"));
                fdd.setFdCreationDate(rs.getDate("fd_creation_date"));
                fdd.setFixedDepositId(rs.getInt("fixed_deposit_id"));
                fdd.setTenure(rs.getInt("tensure"));
                return fdd;
            }
        });
    }

}
```
JdbcTemplate의 update 메서드를 사용해 데이터베이스에 삽입, 갱신, 삭제 연산을 수행한다.<br/>
update 메서드는 PreparedStatementCreator 인스턴스와 KeyHolder 인스턴스를 인수로 받아서 SQL이 성공적으로 실행되면 자동 생성된 키를 반환한다.<br/>
? 위치지정자를 사용해 SQL 문 안에 파라미터를 지정한다.

## NamedParameterJdbcTemplate
NamedParameterJdbcTemplate는 JdbcTemplate 인스턴스를 감싸는 래퍼로, 이를 사용하면 SQL 문 안에서 ? 대신 파라미터 이름을 사용할 수 있다.

```Xml
<bean id="namedJdbcTemplate"
    class="org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate">
    <constructor-arg ref="dataSource" />
</bean>
```
이름이 붙은 파라미터값은 스프링 SqlParameterSource 인터페이스에 의해 구현된다.<br/>
SqlParameterSource 인터페이스의 구현체인 MapSqlParameterSource 클래스는 이름이 붙은 파라미터를 java.util.Map에 저장한다.<br/>
queryForObject는 전달받은 SQL문을 실행해 객체를 하나 반환한다.<br/>
스프링 RowMapper 객체를 사용해 반환받은 로우를 객체로 반환한다.<br/>
이 예제에서 RowMapper는 ResultSet에 있는 로우를 FixedDepositDetails 객체로 반환한다.

## SimpleJdbcInsert
데이터베이스 메타데이터를 활용해 테이블에 로우를 삽입하는 기본 SQL 삽입문을 쉽게 쓸 수 있다.

```Java
@Repository(value = "bankAccountDao")
public class BankAccountDaoImpl implements BankAccountDao {

    private SimpleJdbcInsert insertBankAccountDetail;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private void setDataSource(DataSource dataSource) {
        this.insertBankAccountDetail = new SimpleJdbcInsert(dataSource).withTableName("bank_account_details")
                .usingGeneratedKeyColumns("account_id");
    }

    @Override
    public int createBankAccount(final BankAccountDetails bankAccountDetails) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("balance_amount", bankAccountDetails.getBalanceAmount());
        parameters.put("last_transaction_ts", new Date(bankAccountDetails.getLastTransactionTimestamp().getTime()));
        Number key = insertBankAccountDetail.executeAndReturnKey(parameters);
        return key.intValue();
    }

    @Override
    public void subtractFromAccount(int bankAccountId, int amount) {
        jdbcTemplate.update("update bank_account_details set balance_amount = ? where account_id = ?", amount,
                bankAccountId);
    }

}
```
setDataSource 메서드에 Autowired를 설정해서 DataSource 객체가 dataSource로 전달된다.<br/>
메서드 안에서는 SimpleJdbcInsert 생성자에 DataSource 객체를 전달해 SimpleJdbcInsert 인스턴스를 생성한다.<br/>
withTableName 메서드는 레코드를 삽입하려는 테이블의 이름을 설정한다.<br/>
usingGeneratedKeyColumns 메서드는 자동 생성 키를 포함한 테이블 컬럼의 이름을 설정한다.<br/>
executeAndReturnKey 메서드는 테이블 컬럼 이름과 컬럼에 들어갈 값이 연관되어 들어 있는 Map타입 인수를 받아서 레코드를 삽입한 다음 생성된 키를 반환한다.<br/>
SimpleJdbcInsert 클래스가 내부적으로 JdbcTemplate을 사용해 실제 SQL 삽입 연산을 수행한다.

데이터베이스를 읽고 갱신하는 것 외에도 스프링 JDBC 모듈을 사용할 수 있는 곳
- 저장 프로시저나 함수 실행, 예를 들어 SimpleJdbcCall 클래스를 사용할 수 있다.
- 일괄 갱신(batch update) 실행 : 예를 들어 JdbcTemplate의 batchUpdate 메서드를 통해 같은 PreparedStatement를 통해 여러 데이터베이스 갱신 호출을 일괄 실행할 수 있다.
- 객체지향적인 방식으로 관계형 데이터베이스 접근하기 : 스프링 MappingSqlQuery를 사용하면 ResultSet이 반환한 각 로우를 객체에 매핑할 수 있다.
- 내장 데이터베이스 인스턴스 설정하기 : 스프링 jdbc 스키마를 사용해 HSQL, H2, 더비 등의 데이터베이스 인스턴스를 만들고, 데이터베이스 인스턴스를 DataSource 타입의 빈으로 스프링 컨테이너에 등록할 수 있다.

## 스프링을 통한 트랜잭션 관리
프로그램을 통한 트랜잭션 관리와 선언적인 트랜잭션 관리를 모두 제공한다.
- 프로그램을 통한 트랜잭션 관리 : 스프링의 트랜잭션 관리 추상화를 사용해 명시적으로 트랜잭션을 시작, 종료, 커밋한다.
- 선언적인 트랜잭션 관리 : 스프링 @Transactional을 사용해 트랜잭션 안에서 실행하는 메서드를 지정한다.
