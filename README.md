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

## 요구사항 정리
### 🚀 3단계 - Transaction 적용하기

- [ ] 트랜잭션을 적용할 수 있는 기능을 구현한다.
   - [ ] 비즈니스 로직에서 에러가 발생하면 롤백을 실행한다.
   - [ ] 비즈니스 로직에서 문제가 없으면 커밋을 실행한다.
   - [ ] UserServiceTest 을 성공시키자


