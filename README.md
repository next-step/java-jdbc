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
