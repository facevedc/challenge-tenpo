package tenpo.api.calculation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.test.StepVerifier;
import tenpo.api.calculation.validation.CalculationValidation;
import tenpo.api.common.exception.BadRequestException;

class CalculationValidationTest {

    private final CalculationValidation calculationValidation =
            new CalculationValidation(Validation.buildDefaultValidatorFactory().getValidator());

    @Test
    void shouldValidateCalculationQueryParams() {
        MockServerRequest request = MockServerRequest.builder()
                .queryParam("num1", "5")
                .queryParam("num2", "7")
                .build();

        StepVerifier.create(calculationValidation.validateQueryParams(request))
                .assertNext(result -> {
                    assertEquals("5", result.getNum1().toPlainString());
                    assertEquals("7", result.getNum2().toPlainString());
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenCalculationQueryParamIsInvalid() {
        MockServerRequest request = MockServerRequest.builder()
                .queryParam("num1", "abc")
                .queryParam("num2", "7")
                .build();

        StepVerifier.create(calculationValidation.validateQueryParams(request))
                .expectErrorSatisfies(error -> assertEquals(
                        "Invalid decimal query param: num1",
                        ((BadRequestException) error).getMessage()
                ))
                .verify();
    }

    @Test
    void shouldFailWhenRequiredCalculationQueryParamIsMissing() {
        MockServerRequest request = MockServerRequest.builder()
                .queryParam("num2", "7")
                .build();

        StepVerifier.create(calculationValidation.validateQueryParams(request))
                .expectErrorSatisfies(error -> assertEquals(
                        "num1 is required",
                        ((BadRequestException) error).getMessage()
                ))
                .verify();
    }
}
