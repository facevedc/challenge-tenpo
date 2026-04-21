package tenpo.settings;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory
    ) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        RedisSerializationContext<String, String> serializationContext =
                RedisSerializationContext.<String, String>newSerializationContext(stringRedisSerializer)
                        .key(stringRedisSerializer)
                        .value(stringRedisSerializer)
                        .hashKey(stringRedisSerializer)
                        .hashValue(stringRedisSerializer)
                        .build();

        return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory, serializationContext);
    }
}
