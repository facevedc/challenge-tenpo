package tenpo.domain.history;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import tenpo.domain.history.model.HistoryLogCommand;

class DefaultHistoryRegistrationServiceTest {

    private final HistoryPersistence historyPersistence = mock(HistoryPersistence.class);
    private final DefaultHistoryRegistrationService historyRegistrationService =
            new DefaultHistoryRegistrationService(historyPersistence);

    @Test
    void shouldRegisterHistoryAsynchronously() {
        HistoryLogCommand historyLogCommand = new HistoryLogCommand(
                OffsetDateTime.parse("2026-04-19T10:15:30Z"),
                "/api/v1/calculations",
                "GET",
                "{\"num1\":\"5\",\"num2\":\"7\"}",
                200,
                "{\"final_amount\":13.2}",
                null,
                12L
        );

        when(historyPersistence.save(historyLogCommand)).thenReturn(Mono.empty());

        historyRegistrationService.register(historyLogCommand);

        verify(historyPersistence, timeout(1000)).save(historyLogCommand);
    }

    @Test
    void shouldReturnImmediatelyEvenWhenPersistenceTakesLonger() throws InterruptedException {
        HistoryLogCommand historyLogCommand = new HistoryLogCommand(
                OffsetDateTime.parse("2026-04-19T10:15:30Z"),
                "/api/v1/calculations",
                "GET",
                "{\"num1\":\"5\",\"num2\":\"7\"}",
                200,
                "{\"final_amount\":13.2}",
                null,
                12L
        );
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean completed = new AtomicBoolean(false);

        when(historyPersistence.save(historyLogCommand)).thenReturn(
                Mono.delay(Duration.ofMillis(300))
                        .doOnNext(ignored -> {
                            completed.set(true);
                            countDownLatch.countDown();
                        })
                        .then()
        );

        long startedAt = System.nanoTime();
        historyRegistrationService.register(historyLogCommand);
        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);

        assertTrue(elapsedMillis < 150);
        assertFalse(completed.get());
        assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
        assertTrue(completed.get());
    }

    @Test
    void shouldIgnorePersistenceFailureWhenRegisteringHistory() {
        HistoryLogCommand historyLogCommand = new HistoryLogCommand(
                OffsetDateTime.parse("2026-04-19T10:15:30Z"),
                "/api/v1/calculations",
                "GET",
                "{\"num1\":\"5\",\"num2\":\"7\"}",
                500,
                null,
                "failure",
                12L
        );

        when(historyPersistence.save(historyLogCommand)).thenReturn(Mono.error(new RuntimeException("boom")));

        historyRegistrationService.register(historyLogCommand);

        verify(historyPersistence, timeout(1000)).save(historyLogCommand);
    }
}
