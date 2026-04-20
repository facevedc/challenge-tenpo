package tenpo.domain.calculation;

import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface ExternalPercentageProvider {

    Mono<BigDecimal> getPercentage();
}
