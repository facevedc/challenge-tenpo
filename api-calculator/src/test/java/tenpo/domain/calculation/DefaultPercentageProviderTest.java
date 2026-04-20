package tenpo.domain.calculation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tenpo.domain.calculation.exception.PercentageUnavailableException;
import tenpo.domain.calculation.model.PercentageResult;

@ExtendWith(MockitoExtension.class)
class DefaultPercentageProviderTest {

    @Mock
    private ExternalPercentageProvider externalPercentageProvider;

    @Mock
    private PercentageCacheStore percentageCacheStore;

    private DefaultPercentageProvider defaultPercentageProvider;

    @BeforeEach
    void setUp() {
        defaultPercentageProvider = new DefaultPercentageProvider(externalPercentageProvider, percentageCacheStore, 3, 1);
    }

    @Test
    void shouldReturnExternalPercentageAndCacheIt() {
        when(externalPercentageProvider.getPercentage()).thenReturn(Mono.just(new BigDecimal("10")));
        when(percentageCacheStore.savePercentage(new BigDecimal("10"))).thenReturn(Mono.empty());

        StepVerifier.create(defaultPercentageProvider.getPercentage())
                .expectNext(new PercentageResult(new BigDecimal("10"), "external-mock"))
                .verifyComplete();

        verify(percentageCacheStore).savePercentage(new BigDecimal("10"));
        verify(percentageCacheStore, never()).findPercentage();
    }

    @Test
    void shouldRetryUntilExternalPercentageSucceeds() {
        when(externalPercentageProvider.getPercentage())
                .thenReturn(
                        Mono.error(new RuntimeException("failure-1")),
                        Mono.error(new RuntimeException("failure-2")),
                        Mono.just(new BigDecimal("10"))
                );
        when(percentageCacheStore.savePercentage(new BigDecimal("10"))).thenReturn(Mono.empty());

        StepVerifier.create(defaultPercentageProvider.getPercentage())
                .expectNext(new PercentageResult(new BigDecimal("10"), "external-mock"))
                .verifyComplete();

        verify(externalPercentageProvider, times(3)).getPercentage();
    }

    @Test
    void shouldFallbackToCachedPercentageWhenExternalFails() {
        when(externalPercentageProvider.getPercentage()).thenReturn(Mono.error(new RuntimeException("failure")));
        when(percentageCacheStore.findPercentage()).thenReturn(Mono.just(new BigDecimal("7.5")));

        StepVerifier.create(defaultPercentageProvider.getPercentage())
                .expectNext(new PercentageResult(new BigDecimal("7.5"), "redis-cache"))
                .verifyComplete();

        verify(percentageCacheStore).findPercentage();
    }

    @Test
    void shouldReturnServiceUnavailableWhenExternalFailsAndNoCachedValueExists() {
        when(externalPercentageProvider.getPercentage()).thenReturn(Mono.error(new RuntimeException("failure")));
        when(percentageCacheStore.findPercentage()).thenReturn(Mono.empty());

        StepVerifier.create(defaultPercentageProvider.getPercentage())
                .expectErrorSatisfies(error -> {
                    assertEquals(PercentageUnavailableException.class, error.getClass());
                    assertEquals("Percentage service unavailable and no cached value found", error.getMessage());
                })
                .verify();
    }

    @Test
    void shouldIgnoreCacheWriteFailureAndReturnExternalPercentage() {
        when(externalPercentageProvider.getPercentage()).thenReturn(Mono.just(new BigDecimal("10")));
        when(percentageCacheStore.savePercentage(any(BigDecimal.class)))
                .thenReturn(Mono.error(new RuntimeException("redis write failure")));

        StepVerifier.create(defaultPercentageProvider.getPercentage())
                .expectNext(new PercentageResult(new BigDecimal("10"), "external-mock"))
                .verifyComplete();
    }
}
