package tenpo.domain.calculation;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tenpo.domain.calculation.model.CalculationCommand;
import tenpo.domain.calculation.model.CalculationResult;

@ExtendWith(MockitoExtension.class)
class DefaultCalculationServiceTest {

    @Mock
    private PercentageProvider percentageProvider;

    @InjectMocks
    private DefaultCalculationService defaultCalculationService;

    @Test
    void shouldReturnCalculationResultWithDynamicPercentage() {
        when(percentageProvider.getPercentage()).thenReturn(Mono.just(new BigDecimal("10")));
        StepVerifier.create(
                defaultCalculationService.calculate(
                        new CalculationCommand(new BigDecimal("5"), new BigDecimal("7")))
                )
                .expectNext(new CalculationResult(
                        new BigDecimal("5"),
                        new BigDecimal("7"),
                        new BigDecimal("12"),
                        new BigDecimal("10"),
                        new BigDecimal("13.2"),
                        "external-mock"
                ))
                .verifyComplete();
    }
}
