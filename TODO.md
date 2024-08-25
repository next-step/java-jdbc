# 🚀 4단계 - 트랜잭션 동기화 구현하기

## 미션 설명

UserService에서 changePassword() 메서드를 하나의 트랜잭션으로 처리하려면 Connection 객체가 비즈니스 로직과 섞이게 된다.
이 문제를 해결하기 위해 **트랜잭션 동기화(Transaction synchronization)** 방식을 사용해보자.
트랜잭션 동기화란 트랜잭션을 시작하기 위한 Connection 객체를 따로 보관해두고, DAO에서 호출할 때 저장된 Connection을 가져다 사용하는 방식이다.
`DataSourceUtils`와 `TransactionSynchronizationManager`를 활용하여 DAO가 Connection 객체를 파라미터로 전달받아 사용하지 않도록 만들어보자.

## 기능 요구 사항

### 1. Transaction synchronization 적용하기

서비스와 DAO에서 Connection 객체를 가져오는 부분은 DataSourceUtils를 사용하도록 수정하자.
그리고 TransactionSynchronizationManager 클래스가 올바르게 작동하도록 구현해보자.

```java
public void changePassword(final long id, final String newPassword, final String createdBy) {
    Connection connection = DataSourceUtils.getConnection(dataSource);
    connection.setAutoCommit(false);

    try {
        // todo
        connection.commit();
    } catch (...){
        connection.rollback();
        ...
    } finally{
        DataSourceUtils.releaseConnection(connection, dataSource);
        TransactionSynchronizationManager.unbindResource(dataSource);
    }
}
```

### 생각해보기 🤔

JDBC가 아닌 JPA, JMS 같이 다른 커넥션을 사용하거나 2개 이상의 데이터소스를 하나의 트랜잭션처럼 관리하려면 어떻게 해야 할까?
스프링에서는 이 문제를 PlatformTransactionManager를 사용하여 해결한다.
PlatformTransactionManager가 어떻게 추상화되어 있는지는 스프링 문서를 참고하자.
추가로 로컬 트랜잭션, 글로벌 트랜잭션, JTA 라는 세 가지 키워드도 같이 학습하자.

### 2. 트랜잭션 서비스 추상화하기

트랜잭션 동기화를 적용하여 DAO에게 Connection 객체를 전달하는 코드를 개선할 수 있었다.
하지만 여전히 UserService에 데이터 액세스와 관련된 로직이 남아있다.
인터페이스를 활용하여 트랜잭션 서비스를 추상화하여 비즈니스 로직과 데이터 액세스 로직을 분리해보자.
먼저 아래와 같은 인터페이스를 추가한다.

```java
public interface UserService {

    User findById(final long id);

    void save(final User user);

    void changePassword(final long id, final String newPassword, final String createdBy);
}
```

그리고 UserService 인터페이스를 구현한 클래스 2개를 만든다.

```java
public class AppUserService implements UserService {
    // todo
}
```

```java
...

public class TxUserService implements UserService {

    private final UserService userService;

    // override 대상인 메서드는 userService의 메서드를 그대로 위임(delegate)한다.
    @Override
    public void changePassword(final long id, final String newPassword, final String createdBy) {
        // 트랜잭션 처리 영역

        userService.changePassword(id, newPassword, createdBy);

        // 트랜잭션 처리 영역
    }
}
```

UserServiceTest 클래스의 testTransactionRollback() 테스트 메서드를 아래와 같이 바꿔보자.
그리고 테스트가 통과하도록 만들자.

```
테스트 코드를 통과시키고 미션을 마무리한다.
@Transactional 미션에서 이번에 작성한 코드를 프로덕션 코드으로 적용한다.
```

```java

@Test
void testTransactionRollback() {
    // 트랜잭션 롤백 테스트를 위해 mock으로 교체
    final var userHistoryDao = new MockUserHistoryDao(jdbcTemplate);
    // 애플리케이션 서비스
    final var appUserService = new AppUserService(userDao, userHistoryDao);
    // 트랜잭션 서비스 추상화
    final var userService = new TxUserService(appUserService);

    final var newPassword = "newPassword";
    final var createdBy = "gugu";
    // 트랜잭션이 정상 동작하는지 확인하기 위해 의도적으로 MockUserHistoryDao에서 예외를 발생시킨다.
    assertThrows(DataAccessException.class,
                 () -> userService.changePassword(1L, newPassword, createdBy));

    final var actual = userService.findById(1L);

    assertThat(actual.getPassword()).isNotEqualTo(newPassword);
}

```

### 학습

#### ThreadLocal

https://madplay.github.io/post/java-threadlocal

#### 로컬 트랜잭션

https://docs.spring.io/spring-framework/reference/data-access/transaction/motivation.html

전통적으로 EE 어플리케이션이 선택할 수 있는 트랜잭션 관리 모델이 두가지 있었다고 한다. global or local.

로컬 트랜잭션은 JDBC 같은 리소스 하나에 대한 트랜잭션을 말한다. 사용하기는 쉽지만 여러 리소스에 대한 트랜잭션을 처리할 수 없다는 단점이 있다. 또다른 단점으로는 프로그래밍 모델에 침습적으로 코드를 작성하게
된다고 한다.

#### 글로벌 트랜잭션

글로벌 트랜잭션은 여러 트랜잭션처리를 할 수 있는 리소스들을 하나로 묶어 처리할 수 있도록 해 준다.
관계형 데이터베이스, 메세지 큐 등이 이에 해당한다.

이전에는 글로벌 트랜잭션을 위해 EJB CMT 가 많이 사용되었으나 여러 불편함이 있었다.

스프링에서는 이 두가지 트랜잭션 방식의 단점을 개선한다. 다양한 환경에서 동일한 프로그래밍 모델로 개발할 수 있게 도와준다. 선언적 방식과 프로그래머틱 방식을 모두 지원한다. 프로그래머틱 트랜잭션 관리를 이용하면
개발자는 스프링 트랜잭션 추상화 레벨에서 트랜잭션을 제어할 수 있다. 선언적 트랜잭션 관리를 이용하면 트랜잭션에 관련된 코드를 거의 또는 아예 작성하지 않아도 된다. 이 덕분에 스프링 프레임웍의 트랜잭션 API 나
기타 트랜잭션 API 에 독립적일 수 있다.

#### PlatformTransactionManager

https://docs.spring.io/spring-framework/reference/data-access/transaction/strategies.html

PlatformTransactionManager 은 스프링의 트랜잭션 추상화의 핵심이다.

트랜잭션을 시작/커밋/롤백하는 기능을 제공하고, 트랜잭션을 추상화하여 다룰 수 있게 해 주고, 트랜잭션 전략/격리수준 등을 쉽게 설정할 수 있게 해준다. 단일 인터페이스로 다양한 트랜잭션 API를 다룰 수 있다.
PlatformTransactionManager 을 이용하면 JTA 를 직접 사용하는 것보다 테스트 코드도 훨씬 쉽게 작성할 수 있다.

#### JTA

Java Transaction API 의 준말.
어플리케이션이 분산 트랜잭션을 다룰 수 있도록 지원한다.
표준 자바 플랫폼에서 제공하는 jakarta.transaction.Transaction 을 이용한다.

스프링 프레임웍에는 JtaTransactionManager 라는 클래스가 PlatformTransactionManager 인터페이스를 구현하여 JTA 를 지원한다.

```
org.springframework.transaction.jta.JtaTransactionManager
< abstract class org.springframework.transaction.support.AbstractPlatformTransactionManager
< interface org.springframework.transaction.PlatformTransactionManager
< interface org.springframework.transaction.TransactionManager
```
