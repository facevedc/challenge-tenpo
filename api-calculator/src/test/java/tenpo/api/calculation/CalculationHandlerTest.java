package tenpo.api.calculation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import tenpo.api.calculation.handler.CalculationHandler;
import tenpo.api.calculation.mapper.CalculationDomainMapper;
import tenpo.api.calculation.mapper.CalculationResponseMapper;
import tenpo.api.calculation.validation.CalculationValidation;
import tenpo.api.common.error.GlobalExceptionHandler;
import tenpo.api.history.handler.HistoryHandler;
import tenpo.domain.calculation.CalculationService;
import tenpo.domain.calculation.exception.PercentageUnavailableException;
import tenpo.domain.calculation.model.CalculationCommand;
import tenpo.domain.calculation.model.CalculationResult;
import tenpo.domain.history.HistoryRegistrationService;
import tenpo.domain.history.model.HistoryLogCommand;
import tenpo.settings.HttpHeaderConstants;
import tenpo.settings.JacksonConfig;
import tenpo.settings.RouterConfig;
import tenpo.settings.SwaggerConfig;

@WebFluxTest
@Import({
        JacksonConfig.class,
        SwaggerConfig.class,
        RouterConfig.class,
        CalculationHandler.class,
        CalculationValidation.class,
        CalculationDomainMapper.class,
        CalculationResponseMapper.class,
        GlobalExceptionHandler.class
})
@ImportAutoConfiguration(exclude = {
        R2dbcAutoConfiguration.class,
        RedisReactiveAutoConfiguration.class
})
class CalculationHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CalculationService calculationService;

    @MockitoBean
    private HistoryHandler historyHandler;

    @MockitoBean
    private HistoryRegistrationService historyRegistrationService;

    @Test
    void shouldReturnCalculationContract() {
        when(calculationService.calculate(new CalculationCommand(new BigDecimal("5"), new BigDecimal("7"))))
                .thenReturn(Mono.just(new CalculationResult(
                        new BigDecimal("5"),
                        new BigDecimal("7"),
                        new BigDecimal("12"),
                        new BigDecimal("10"),
                        new BigDecimal("13.2"),
                        "external-mock"
                )));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/calculations")
                        .queryParam("num1", "5")
                        .queryParam("num2", "7")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.num1").isEqualTo(5)
                .jsonPath("$.num2").isEqualTo(7)
                .jsonPath("$.base_sum").isEqualTo(12)
                .jsonPath("$.percentage").isEqualTo(10)
                .jsonPath("$.final_amount").isEqualTo(13.2)
                .jsonPath("$.percentage_source").isEqualTo("external-mock");

        verify(historyRegistrationService, timeout(1000)).register(any(HistoryLogCommand.class));
    }

    @Test
    void shouldSupportMockScenarioHeaderForLocalTests() {
        when(calculationService.calculate(new CalculationCommand(new BigDecimal("5"), new BigDecimal("7"))))
                .thenReturn(Mono.just(new CalculationResult(
                        new BigDecimal("5"),
                        new BigDecimal("7"),
                        new BigDecimal("12"),
                        new BigDecimal("10"),
                        new BigDecimal("13.2"),
                        "external-mock"
                )));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/calculations")
                        .queryParam("num1", "5")
                        .queryParam("num2", "7")
                        .build())
                .header(HttpHeaderConstants.MOCK_SCENARIO, "success")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.percentage_source").isEqualTo("external-mock");
    }

    @Test
    void shouldIgnoreBlankMockScenarioHeader() {
        when(calculationService.calculate(new CalculationCommand(new BigDecimal("5"), new BigDecimal("7"))))
                .thenReturn(Mono.just(new CalculationResult(
                        new BigDecimal("5"),
                        new BigDecimal("7"),
                        new BigDecimal("12"),
                        new BigDecimal("10"),
                        new BigDecimal("13.2"),
                        "external-mock"
                )));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/calculations")
                        .queryParam("num1", "5")
                        .queryParam("num2", "7")
                        .build())
                .header(HttpHeaderConstants.MOCK_SCENARIO, "")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.percentage_source").isEqualTo("external-mock");
    }

    @Test
    void shouldFailWhenCalculationQueryParamIsInvalid() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/calculations")
                        .queryParam("num1", "abc")
                .queryParam("num2", "7")
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BAD_REQUEST")
                .jsonPath("$.message").isEqualTo("Invalid decimal query param: num1");
    }

    @Test
    void shouldFailWhenRequiredCalculationQueryParamIsMissing() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/calculations")
                        .queryParam("num2", "7")
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BAD_REQUEST")
                .jsonPath("$.message").isEqualTo("num1 is required");
    }

    @Test
    void shouldReturnServiceUnavailableWhenPercentageIsNotAvailable() {
        when(calculationService.calculate(new CalculationCommand(new BigDecimal("5"), new BigDecimal("7"))))
                .thenReturn(Mono.error(new PercentageUnavailableException(
                        "Percentage service unavailable and no cached value found"
                )));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/calculations")
                        .queryParam("num1", "5")
                        .queryParam("num2", "7")
                        .build())
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.code").isEqualTo("SERVICE_UNAVAILABLE")
                .jsonPath("$.message").isEqualTo("Percentage service unavailable and no cached value found");

        verify(historyRegistrationService, timeout(1000)).register(any(HistoryLogCommand.class));
    }
}
