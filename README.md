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
- [x] RowMapper 인터페이스 구현
- [x] JdbcTemplate 클래스 구현
  - [x] findAll 메서드 구현
  - [x] findById 메서드 구현
  - [x] findByAccount 메서드 구현
  - [x] insert 메서드 구현
  - [x] update 메서드 구현

## 2단계 - 리팩터링
- [x] Unchecked Exception을 사용해 예외 처리를 간결하게 구현한다.
- [x] RowMapper 인터페이스를 제네릭으로 사용한다.
- [x] PreparedStatementSetter 인터페이스를 가변인자로 사용한다.

## 3단계 - Transaction 적용하기
- [x] UserService에서 예외가 발생시에 롤백을 한다.
- [x] Connection 객체의 setAutoCommit(false) 메서드를 호출하여 commit 실행을 제한한다.
- [x] userDao와 userHistoryDao를 한 트랜잭션으로 묶으려면 동일한 Connection 객체를 사용하도록 변경한다.
- [x] 실제 패스워드가 변경되는지 testChangePassword() 테스트를 통과하기

## 4단계 - 트랜잭션 동기화 구현하기
- [x] Transaction synchronization 적용하기
- [ ] 트랜잭션 서비스 추상화하기
- [ ] testTransactionRollback() 테스트를 통과하기