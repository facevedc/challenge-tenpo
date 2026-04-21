package tenpo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tenpo.domain.history.HistoryPersistence;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration," +
                "org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
class CalculatorApplicationTests {

    @MockitoBean
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @MockitoBean
    private HistoryPersistence historyPersistence;

    @Test
    void contextLoads() {
    }
}
