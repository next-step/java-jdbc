# 만들면서 배우는 스프링

[Next Step - 과정 소개](https://edu.nextstep.camp/c/4YUvqn9V)

## JDBC 라이브러리 구현하기

### 학습목표

- JDBC 라이브러리를 구현하는 경험을 함으로써 중복을 제거하는 연습을 한다.
- Transaction 적용을 위해 알아야할 개념을 이해한다.

### 시작 가이드

1. 이전 미션에서 진행한 코드를 사용하고 싶다면, 마이그레이션 작업을 진행합니다.
    - 학습 테스트는 강의 시간에 풀어봅시다.
2. LMS의 1단계 미션부터 진행합니다.

## 준비 사항

- 강의 시작 전에 docker를 설치해주세요.

## 학습 테스트

1. [ConnectionPool](study/src/test/java/connectionpool)
2. [Transaction](study/src/test/java/transaction)

## 1단계 - JDBC 라이브러리 구현하기

- SQL 쿼리 작성, 쿼리에 전달할 인자, SELECT 구문일 경우 조회 결과를 추출하는 것만 집중할 수 있도록 라이브러리를 만들자.
- UserDaoTest 를 활용해 진행한다.
- 중복을 제거하기 위한 라이브러리는 JdbcTemplate 클래스에 구현한다.
- DataSource는 DataSourceConfig 클래스의 getInstance() 메서드를 호출하면 된다.

### 요구사항 정리

- UserDao 에 있는 insert, update 메서드들의 공통된 부분을 JdbcTemplate 클래스로 분리한다.
    - Connection, PreparedStatement 를 가져온다
    - PreparedStatement 에 값을 셋팅한다
    - 쿼리를 실행한다.
    - PreparedStatement, Connection 을 닫는다
    - try-catch-finally 로 예외처리를 한다.
- UserDao 에 있는 findAll, findById, findByAccount 메서드들의 공통된 부분을 JdbcTemplate 클래스로 분리한다.
    - Connection, PreparedStatement, ResultSet 을 가져온다
    - PreparedStatement 에 값을 셋팅한다
    - 쿼리를 실행한다.
    - ResultSet 으로 반환 값을 셋팅한다
    - ResultSet, PreparedStatement, Connection 을 닫는다
    - try-catch-finally 로 예외처리를 한다.

- JdbcTemplate
    - update
        - insert, update, delete 하는 책임을 가진다.
    - query
        - select 을 하는 책임을 가진다.
    - queryForObject
        - 단일 select 을 하는 책임을 가진다.
- RowMapper
    - ResultSet 에 나온 결과를 맵핑한 뒤 객체를 반환한다.

## 2단계 - 리팩터링

### 요구사항 정리

- IndexedQueryBuilder
    - "?" 기반 sql 를 만들어주는 query builder
- PrepareStatementSetter
    - IndexedPrepareStatementSetter
        - prepareStatement 를 이용해 "?" 로 되어있는 sql 을 index 기반으로 치환한다.

## 3단계 - Transaction 적용하기

### 요구사항

- `UserService.changePassword` 가 원자성(Atomic) 을 보장하도록 트랜잭션을 적용한다.
- `Connection.setAutoCommit(false)` 를 통해 트랜잭션을 시작한다.
- 비지니스 로직을 실행한다.
- 정상적으로 종료되면 `Connection.commit()` 으로 커밋한다.
- 예외가 발생하면 `Connection.rollback()` 으로 롤백 한 뒤 예외를 던진다.
- userDao 와 userHistoryDao 가 하나의 Connection 객체를 사용하도록 한다.

- TransactionSynchronizationManager
    - 실행 중인 Thread 의 DataSource 별 Connection 을 관리하는 객체
- TransactionTemplate
    - TransactionSynchronizationManager 를 이용해서 connection 을 사용하는 객체
    - 정상 처리시 commit 을 한다.
    - 예외 발생 시 rollback 을 한다.
    - 종료 시 connection 을 닫고 TransactionSynchronizationManager 에 connection 을 제거한다.

## 4단계 - 트랜잭션 동기화 구현하기

### 요구사항

- Transaction synchronization 적용하기
    - Connection 객체를 가져오는 부분은 `DataSourceUtils` 를 사용하도록 수정한다.
    - `TransactionSynchronizationManager` 를 통해 Thread 별로 관리하도록 한다.
- 인터페이스를 활용하여 트랜잭션 서비스를 추상화 해 비지니스 로직과 데이터 액세스 로직을 분리한다.
    - `TxUserService` 를 활용해 트랜잭션 처리를 하고 내부 로직은 위임한다.
    - 아래의 테스트가 통과하도록 바꾸자

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

### 생각해보기

- PlatformTransactionManager 가 어떻게 추상화되어 있는가?
    - DataSourceTransactionManager
      - JDBC 를 사용한 로컬 트랜잭션 관리
      - 하나의 데이터 소스를 대상으로 트랜잭션을 관리
    - JpaTransactionManager
      - JPA 를 사용하는 애플리케이션에서 트랜잭션 관리
      - JPA EntityManager 를 통해 트랜잭션을 관리
    - JmsTransactionManager
      - JMS(Java Message Service)에서의 트랜잭션 관리
    - JtaTransactionManager
      - JTA(Java Transaction API)를 사용한 글로벌 트랜잭션 관리
      - 여러 리소스를 하나의 트랜잭션으로 묶어 관리
