package tenpo.domain.calculation;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tenpo.domain.calculation.model.CalculationCommand;
import tenpo.domain.calculation.model.CalculationResult;

@Service
public class DefaultCalculationService implements CalculationService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final PercentageProvider percentageProvider;

    public DefaultCalculationService(PercentageProvider percentageProvider) {
        this.percentageProvider = percentageProvider;
    }

    @Override
    public Mono<CalculationResult> calculate(CalculationCommand calculationCommand) {
        BigDecimal baseSum = calculationCommand.num1().add(calculationCommand.num2());
        return percentageProvider.getPercentage()
                .map(percentage -> new CalculationResult(
                        calculationCommand.num1(),
                        calculationCommand.num2(),
                        baseSum,
                        percentage,
                        applyPercentage(baseSum, percentage),
                        "external-mock"
                ));
    }

    private BigDecimal applyPercentage(BigDecimal baseSum, BigDecimal percentage) {
        BigDecimal percentageAmount = baseSum.multiply(percentage).divide(ONE_HUNDRED);
        return baseSum.add(percentageAmount);
    }
}
