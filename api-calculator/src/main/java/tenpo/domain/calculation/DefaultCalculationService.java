package tenpo.domain.calculation;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tenpo.domain.calculation.model.CalculationCommand;
import tenpo.domain.calculation.model.CalculationResult;

@Service
public class DefaultCalculationService implements CalculationService {

    @Override
    public Mono<CalculationResult> calculate(CalculationCommand calculationCommand) {
        BigDecimal baseSum = calculationCommand.num1().add(calculationCommand.num2());
        BigDecimal bootstrapPercentage = BigDecimal.ZERO;

        return Mono.just(new CalculationResult(
                calculationCommand.num1(),
                calculationCommand.num2(),
                baseSum,
                bootstrapPercentage,
                baseSum,
                "bootstrap"
        ));
    }
}
