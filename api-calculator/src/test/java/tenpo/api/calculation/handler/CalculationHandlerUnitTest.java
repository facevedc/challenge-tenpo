package tenpo.api.calculation.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tenpo.api.calculation.mapper.CalculationDomainMapper;
import tenpo.api.calculation.mapper.CalculationResponseMapper;
import tenpo.api.calculation.validation.CalculationValidation;
import tenpo.api.common.error.GlobalExceptionHandler;
import tenpo.domain.calculation.CalculationService;
import tenpo.domain.calculation.model.CalculationCommand;
import tenpo.domain.calculation.model.CalculationResult;
import tenpo.domain.history.HistoryRegistrationService;
import tenpo.domain.history.model.HistoryLogCommand;

class CalculationHandlerUnitTest {

    private final CalculationService calculationService = mock(CalculationService.class);
    private final HistoryRegistrationService historyRegistrationService = mock(HistoryRegistrationService.class);
    private final ObjectMapper objectMapper = mock(ObjectMapper.class);
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final CalculationHandler calculationHandler = new CalculationHandler(
            calculationService,
            new CalculationValidation(validator),
            new CalculationDomainMapper(),
            new CalculationResponseMapper(),
            new GlobalExceptionHandler(),
            historyRegistrationService,
            objectMapper
    );

    @Test
    void shouldFallbackToStringWhenHistorySerializationFails() throws Exception {
        when(calculationService.calculate(new CalculationCommand(new BigDecimal("5"), new BigDecimal("7"))))
                .thenReturn(Mono.just(new CalculationResult(
                        new BigDecimal("5"),
                        new BigDecimal("7"),
                        new BigDecimal("12"),
                        new BigDecimal("10"),
                        new BigDecimal("13.2"),
                        "external-mock"
                )));
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom") { });

        MockServerRequest request = MockServerRequest.builder()
                .uri(URI.create("http://localhost/api/v1/calculations?num1=5&num2=7"))
                .queryParam("num1", "5")
                .queryParam("num2", "7")
                .build();

        StepVerifier.create(calculationHandler.calculate(request))
                .assertNext(response -> org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();

        verify(historyRegistrationService).register(any(HistoryLogCommand.class));
    }
}
