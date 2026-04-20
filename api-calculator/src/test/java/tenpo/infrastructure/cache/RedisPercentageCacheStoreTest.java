package tenpo.infrastructure.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RedisPercentageCacheStoreTest {

    @Mock
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> reactiveValueOperations;

    private RedisPercentageCacheStore redisPercentageCacheStore;

    @BeforeEach
    void setUp() {
        redisPercentageCacheStore = new RedisPercentageCacheStore(reactiveStringRedisTemplate, 30);
    }

    @Test
    void shouldReturnCachedPercentageWhenCacheHitOccurs() {
        when(reactiveStringRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.get("tenpo:calculator:percentage")).thenReturn(Mono.just("10"));

        StepVerifier.create(redisPercentageCacheStore.findPercentage())
                .assertNext(percentage -> assertEquals(new BigDecimal("10"), percentage))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenCacheMissOccurs() {
        when(reactiveStringRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.get("tenpo:calculator:percentage")).thenReturn(Mono.empty());

        StepVerifier.create(redisPercentageCacheStore.findPercentage())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldSavePercentageWithTtlOfThirtyMinutes() {
        when(reactiveStringRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
        when(reactiveValueOperations.set("tenpo:calculator:percentage", "10", Duration.ofMinutes(30)))
                .thenReturn(Mono.just(true));

        StepVerifier.create(redisPercentageCacheStore.savePercentage(new BigDecimal("10")))
                .verifyComplete();

        verify(reactiveValueOperations).set("tenpo:calculator:percentage", "10", Duration.ofMinutes(30));
    }
}
