package tenpo.domain.calculation;

import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import tenpo.domain.calculation.exception.PercentageUnavailableException;
import tenpo.domain.calculation.model.PercentageResult;

@Service
public class DefaultPercentageProvider implements PercentageProvider {

    private static final String EXTERNAL_MOCK_SOURCE = "external-mock";
    private static final String REDIS_CACHE_SOURCE = "redis-cache";

    private final ExternalPercentageProvider externalPercentageProvider;
    private final PercentageCacheStore percentageCacheStore;
    private final int maxAttempts;
    private final long retryDelayMillis;

    public DefaultPercentageProvider(
            ExternalPercentageProvider externalPercentageProvider,
            PercentageCacheStore percentageCacheStore,
            @Value("${tenpo.client.percentage.max-attempts:3}") int maxAttempts,
            @Value("${tenpo.client.percentage.retry-delay-millis:100}") long retryDelayMillis
    ) {
        this.externalPercentageProvider = externalPercentageProvider;
        this.percentageCacheStore = percentageCacheStore;
        this.maxAttempts = maxAttempts;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public Mono<PercentageResult> getPercentage() {
        return Mono.defer(externalPercentageProvider::getPercentage)
                .retryWhen(Retry.fixedDelay(maxAttempts - 1, Duration.ofMillis(retryDelayMillis)))
                .flatMap(this::cacheAndReturn)
                .onErrorResume(this::fallbackToCache);
    }

    private Mono<PercentageResult> cacheAndReturn(BigDecimal percentage) {
        return percentageCacheStore.savePercentage(percentage)
                .onErrorResume(throwable -> Mono.empty())
                .thenReturn(new PercentageResult(percentage, EXTERNAL_MOCK_SOURCE));
    }

    private Mono<PercentageResult> fallbackToCache(Throwable throwable) {
        return percentageCacheStore.findPercentage()
                .map(percentage -> new PercentageResult(percentage, REDIS_CACHE_SOURCE))
                .switchIfEmpty(Mono.error(new PercentageUnavailableException(
                        "Percentage service unavailable and no cached value found"
                )));
    }
}
