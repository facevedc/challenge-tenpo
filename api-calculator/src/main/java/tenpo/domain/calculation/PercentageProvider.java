package tenpo.domain.calculation;

import reactor.core.publisher.Mono;
import tenpo.domain.calculation.model.PercentageResult;

public interface PercentageProvider {

    Mono<PercentageResult> getPercentage();
}
