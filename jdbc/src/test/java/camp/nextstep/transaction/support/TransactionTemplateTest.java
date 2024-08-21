package camp.nextstep.transaction.support;

import camp.nextstep.transaction.PlatformTransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TransactionTemplateTest {

    private final PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @DisplayName("메서드가 정상적으로 실행 되고 트랜잭션이 commit 된다.")
    void runWithoutReturnTest() {
        final RunnableClass runnableClass = new RunnableClass();

        transactionTemplate.run(runnableClass);

        assertThat(runnableClass.counter).isEqualTo(1);
        verify(transactionManager).commit();
    }

    private static class RunnableClass implements Runnable {
        private int counter;

        @Override
        public void run() {
            counter++;
        }
    }

    @Test
    @DisplayName("메서드가 정상적으로 실행 되어 값을 반환 하고 트랜잭션이 commit 된다.")
    void runWithReturnTest() {
        final String result = transactionTemplate.run(() -> "success");

        assertThat(result).isEqualTo("success");
        verify(transactionManager).commit();
    }

    @Test
    @DisplayName("메서드 실행 중 예외가 발생하면 롤백이 실행된다.")
    void runExceptionTest() {
        assertThatThrownBy(() ->
                transactionTemplate.run(() -> {
                    throw new RuntimeException();
                }))
                .isInstanceOf(RuntimeException.class);

        verify(transactionManager).rollback();
    }

}
