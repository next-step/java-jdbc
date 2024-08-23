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



## 1단계 요구사항


1. [x] UserDaoTest를 이용해서 리팩터링 진행
2. [x] JdbcTemplate을 구현한다.  
   - RowMapper 구현  (과거에 MetaEntity에 값 넣었던것이랑 동일)
   - JdbcTemplate의 메서드들 생성
     - queryForObejct
     - query
3. [x] 위의 기능들을 테스트로 검증한다.
     

