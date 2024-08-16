package camp.nextstep.study;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ThreadLocalTest {
    private final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    @DisplayName("ThreadLocal은 각 Thread가 고유한 변수를 독립적으로 사용할 수 있게 해준다. (스레드 간 공유 X)")
    @Test
    void test() throws InterruptedException {
        CountDownLatch setLatch = new CountDownLatch(10);
        CountDownLatch getLatch = new CountDownLatch(10);

        threadLocal.set(Thread.currentThread().getName());

        try (ExecutorService threadPool = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < 10; i++) {
                threadPool.submit(() -> {
                    String threadName = Thread.currentThread().getName();
                    threadLocal.set(threadName);
                    setLatch.countDown();
                });
            }
            setLatch.await();

            for (int i = 0; i < 10; i++) {
                threadPool.submit(() -> {
                    스레드_이름과_스레드_로컬에서_꺼내온_값_검증();
                    getLatch.countDown();
                });
            }
            getLatch.await();
            스레드_이름과_스레드_로컬에서_꺼내온_값_검증();
        }
    }

    private void 스레드_이름과_스레드_로컬에서_꺼내온_값_검증() {
        String threadName = Thread.currentThread().getName();
        String threadNameInThreadLocal = threadLocal.get();
        System.out.printf("현재 스레드 이름: [%s], 스레드 로컬에서 꺼내온 스레드 이름: [%s]%n", threadName, threadNameInThreadLocal);
        assertThat(threadName).isEqualTo(threadNameInThreadLocal);
    }
}
