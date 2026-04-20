package tenpo.domain.calculation;

import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface PercentageProvider {

    Mono<BigDecimal> getPercentage();
}
