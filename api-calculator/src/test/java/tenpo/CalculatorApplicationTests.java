package tenpo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration," +
                "org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
class CalculatorApplicationTests {

    @Test
    void contextLoads() {
    }
}
