package tenpo.domain.calculation;

import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface PercentageCacheStore {

    Mono<BigDecimal> findPercentage();

    Mono<Void> savePercentage(BigDecimal percentage);
}
