package tenpo.api.history;

import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
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
import tenpo.api.common.error.GlobalExceptionHandler;
import tenpo.api.history.mapper.HistoryDomainMapper;
import tenpo.api.history.mapper.HistoryResponseMapper;
import tenpo.api.history.handler.HistoryHandler;
import tenpo.api.history.validation.HistoryValidation;
import tenpo.domain.history.HistoryQueryService;
import tenpo.domain.history.model.HistoryEntry;
import tenpo.domain.history.model.HistoryPage;
import tenpo.domain.history.model.HistoryQuery;
import tenpo.settings.JacksonConfig;
import tenpo.settings.RouterConfig;
import tenpo.settings.SwaggerConfig;

@WebFluxTest
@Import({
        JacksonConfig.class,
        SwaggerConfig.class,
        RouterConfig.class,
        HistoryHandler.class,
        HistoryValidation.class,
        HistoryDomainMapper.class,
        HistoryResponseMapper.class,
        GlobalExceptionHandler.class
})
@ImportAutoConfiguration(exclude = {
        R2dbcAutoConfiguration.class,
        RedisReactiveAutoConfiguration.class
})
class HistoryHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private HistoryQueryService historyQueryService;

    @MockitoBean
    private CalculationHandler calculationHandler;

    @Test
    void shouldReturnHistoryContract() {
        HistoryEntry entry = new HistoryEntry(
                1L,
                OffsetDateTime.parse("2026-04-19T10:15:30Z"),
                "/api/v1/calculations",
                "GET",
                "num1=5&num2=7",
                "SUCCESS"
        );

        when(historyQueryService.findHistory(new HistoryQuery(0, 20)))
                .thenReturn(Mono.just(new HistoryPage(List.of(entry), 0, 20, 1, 1)));

        webTestClient.get()
                .uri("/api/v1/history?page=0&size=20")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(20)
                .jsonPath("$.total_elements").isEqualTo(1)
                .jsonPath("$.total_pages").isEqualTo(1)
                .jsonPath("$.items[0].id").isEqualTo(1)
                .jsonPath("$.items[0].endpoint").isEqualTo("/api/v1/calculations")
                .jsonPath("$.items[0].http_method").isEqualTo("GET")
                .jsonPath("$.items[0].request_summary").isEqualTo("num1=5&num2=7")
                .jsonPath("$.items[0].outcome").isEqualTo("SUCCESS");
    }

    @Test
    void shouldUseDefaultPaginationValues() {
        when(historyQueryService.findHistory(new HistoryQuery(0, 20)))
                .thenReturn(Mono.just(new HistoryPage(List.of(), 0, 20, 0, 0)));

        webTestClient.get()
                .uri("/api/v1/history")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(20)
                .jsonPath("$.total_elements").isEqualTo(0)
                .jsonPath("$.total_pages").isEqualTo(0);
    }

    @Test
    void shouldFailWhenHistoryPaginationIsInvalid() {
        webTestClient.get()
                .uri("/api/v1/history?page=invalid&size=20")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BAD_REQUEST")
                .jsonPath("$.message").isEqualTo("Invalid integer query param: page");
    }

    @Test
    void shouldFailWhenHistoryPageIsNegative() {
        webTestClient.get()
                .uri("/api/v1/history?page=-1&size=20")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BAD_REQUEST")
                .jsonPath("$.message").isEqualTo("page must be greater than or equal to 0");
    }
}
