# 🚀 3단계 - Transaction 적용하기

## 미션 설명

User의 비밀번호를 변경할 수 있는 기능을 추가하자.
해당 기능은 UserService 클래스의 changePassword() 메서드에 구현되어있다.
비밀번호를 변경하면 누가, 언제, 어떤 비밀번호로 바꿨는지 이력을 남겨야 한다.
이력이 있어야 고객센터에서 고객 문의를 대응할 수 있다.
고객의 변경 이력을 확인 할 수 있도록 changePassword() 메서드는 비밀번호 변경과 이력을 남기도록 구현되어 있다.

하지만 changePassword() 메서드는 원자성(Atomic)이 보장되지 않는다.
중간에 예외가 발생해서 작업을 완료할 수 없다면 작업을 원래 상태로 되돌려야 한다.
즉, 비밀번호를 바꾸고 이력을 남기는 도중에 에러가 발생하면 원래 비밀번호로 돌려놔야한다.
원자성을 보장하기 위해 트랜잭션을 적용하자

## 기능 요구 사항

```
UserServiceTest 클래스에서 @Disabled를 삭제하고 미션을 진행한다.
```

### 트랜잭션 경계 설정하기

[Java Tutorials - Using Transactions](https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html)

JDBC API로 어떻게 트랜잭션을 시작하고 커밋, 롤백을 할 수 있을까?
Connection 객체의 setAutoCommit(false) 메서드를 호출하면 트랜잭션이 시작된다.
비즈니스 로직이 끝나면 반드시 트랜잭션 커밋 또는 롤백을 실행한다.
이처럼 트랜잭션을 시작하고 끝나는 부분을 트랜잭션 경계라고 한다.

현재 userDao와 userHistoryDao는 각각 Connection 객체를 만들기 때문에 개별적으로 트랜잭션이 생성된다.

userDao와 userHistoryDao를 한 트랜잭션으로 묶으려면 동일한 Connection 객체를 사용하도록 변경하자.
