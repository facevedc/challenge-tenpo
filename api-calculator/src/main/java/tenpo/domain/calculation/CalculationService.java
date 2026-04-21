package tenpo.domain.calculation;

import reactor.core.publisher.Mono;
import tenpo.domain.calculation.model.CalculationCommand;
import tenpo.domain.calculation.model.CalculationResult;

public interface CalculationService {

    Mono<CalculationResult> calculate(CalculationCommand calculationCommand);
}
