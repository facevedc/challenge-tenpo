package tenpo.infrastructure.cache;

import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tenpo.domain.calculation.PercentageCacheStore;

@Component
public class RedisPercentageCacheStore implements PercentageCacheStore {

    private static final String CACHE_KEY = "tenpo:calculator:percentage";

    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private final long percentageTtlMinutes;

    public RedisPercentageCacheStore(
            ReactiveStringRedisTemplate reactiveStringRedisTemplate,
            @Value("${tenpo.cache.percentage-ttl-minutes:30}") long percentageTtlMinutes
    ) {
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        this.percentageTtlMinutes = percentageTtlMinutes;
    }

    @Override
    public Mono<BigDecimal> findPercentage() {
        return reactiveStringRedisTemplate.opsForValue()
                .get(CACHE_KEY)
                .map(BigDecimal::new);
    }

    @Override
    public Mono<Void> savePercentage(BigDecimal percentage) {
        return reactiveStringRedisTemplate.opsForValue()
                .set(CACHE_KEY, percentage.toPlainString(), Duration.ofMinutes(percentageTtlMinutes))
                .then();
    }
}
