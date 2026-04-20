package tenpo.api.calculation;

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
import tenpo.domain.calculation.model.CalculationCommand;
import tenpo.domain.calculation.model.CalculationResult;
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

    @Test
    void shouldReturnCalculationContract() {
        when(calculationService.calculate(new CalculationCommand(new BigDecimal("5"), new BigDecimal("7"))))
                .thenReturn(Mono.just(new CalculationResult(
                        new BigDecimal("5"),
                        new BigDecimal("7"),
                        new BigDecimal("12"),
                        BigDecimal.ZERO,
                        new BigDecimal("12"),
                        "bootstrap"
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
                .jsonPath("$.percentage").isEqualTo(0)
                .jsonPath("$.final_amount").isEqualTo(12)
                .jsonPath("$.percentage_source").isEqualTo("bootstrap");
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
}
