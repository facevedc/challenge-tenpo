package tenpo.domain.calculation;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import tenpo.domain.calculation.model.CalculationCommand;
import tenpo.domain.calculation.model.CalculationResult;

class DefaultCalculationServiceTest {

    private final DefaultCalculationService defaultCalculationService = new DefaultCalculationService();

    @Test
    void shouldReturnBootstrapCalculationResult() {
        StepVerifier.create(
                defaultCalculationService.calculate(
                        new CalculationCommand(new BigDecimal("5"), new BigDecimal("7")))
                )
                .expectNext(new CalculationResult(
                        new BigDecimal("5"),
                        new BigDecimal("7"),
                        new BigDecimal("12"),
                        BigDecimal.ZERO,
                        new BigDecimal("12"),
                        "bootstrap"
                ))
                .verifyComplete();
    }
}
