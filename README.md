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
- 쿼리가 동작하도록 수정한다

- findByAccount
```mysql
select id, account, password, email from users where account = ? 
```
- findAll
  - resultSet에서 next로 나오는 모든 유저를 list로 반환한다.
```mysql
select id, account, password, email from users 
```
- update
```mysql
update users set account = ?, password = ?, email = ? where id = ?
```

- RowMapper
  - ResultSet을 파라미터로 받아 제네릭 선언된 타입의 객체를 반환하는 스펙을 가진다
  - 구현체는 ResultSet을 받아 타입에 맞는 객체를 파싱하여 반환한다
- JdbcTemplate
  - query
    - RowMapper와 sql문을 받아 실행한다
    - argument array를 받을 수 있으며 이를 받는 경우 PreparedStatement에 값을 할당해준다
    - 반환되는 값이 List인 경우 List에 모든 값을 담아 반환한다
    - 반환되는 값이 Object인 경우 객체에 담아서 내보내며 값이 없는 경우 empty가 반환된다.
    - sql 쿼리에 쿼리 placeholder가 갯수만큼 없는 경우 예외를 던진다
    - sql이 null혹은 빈 문자열인 경우 예외를 던진다
  - update
    - sql과 파라미터를 받아 실행한다
    - sql 쿼리에 쿼리 placeholder가 갯수만큼 없는 경우 예외를 던진다
    - sql이 null혹은 빈 문자열인 경우 예외를 던진다

  - PreparedStatementParser
    - connection을 받아 preparedStatement를 생성하고 이를 executeQuery 하는 처리를 추상화
  - ResultSet
    - PreparedStatement로 생성된 결과인 ResultSet을 rowmapper로 인스턴스화하는 처리를 추상화

- StringUtils
  - 입력된 문자열에 요청된 sequence가 몇개있는지 계산한다
- SqlType
  - Types에 지정된 sql type value와 매핑되는 java type을 가진다
  - 지원하지 않는 sqlTypeValue로 생성하려하면 예외를 던진다
  - 요청된 객체가 SqlType의 java type과 일치하는지 확인한다

## 2단계 - 리팩터링
- ResultSetSetter
  - PreparedStatement로 ResultSet을 만들어 객체로 파싱하는 역할을 가져간다
- PreparedStatementSetter
  - dataSource로 connection을 가져와 ParparedStementParser로 실행하는 역할을 가져간다
  - 에러 발생 시 발생한 에러에 대한 로그를 남긴다
  - 에러 발생 시 checkedException을 uncheckedException으로 변환하여 던진다
  - 요청된 쿼리 실행에 필요한 파라미터수와 불일치하는 경우 예외를 던진다

## 3단계 - Transaction 적용하기
- 트랜잭션 단위를 구성한다
  - UserService에서 로직 실행 시 예외가 발생하면 롤백한다
  - 트랜잭션 시작 시 auto commit를 false로 두어 commit 실행을 제한한다
  - 비즈니스 실행이 모두 종료되었다면 commit한다
  - 트랜잭션에 묶이는 dao들이 모두 같은 connection을 사용하도록 한다
