package tenpo.infrastructure.client.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tenpo.settings.HttpHeaderConstants;

class MockPercentageClientTest {

    @Test
    void shouldReturnPercentageFromExternalMockService() {
        AtomicReference<String> scenarioHeader = new AtomicReference<>();

        ExchangeFunction exchangeFunction = clientRequest -> Mono.just(
                ClientResponse.create(HttpStatus.OK)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("{\"percentage\":10}")
                        .build()
        );

        ExchangeFunction captureExchangeFunction = clientRequest -> {
            scenarioHeader.set(clientRequest.headers().getFirst(HttpHeaderConstants.MOCK_SCENARIO));
            return exchangeFunction.exchange(clientRequest);
        };

        WebClient webClient = WebClient.builder()
                .exchangeFunction(captureExchangeFunction)
                .build();

        MockPercentageClient mockPercentageClient =
                new MockPercentageClient(webClient, "http://localhost:8081");

        StepVerifier.create(mockPercentageClient.getPercentage())
                .assertNext(percentage -> assertEquals(new BigDecimal("10"), percentage))
                .verifyComplete();

        assertNull(scenarioHeader.get());
    }

    @Test
    void shouldForwardMockScenarioHeaderWhenPresentInReactiveContext() {
        AtomicReference<String> scenarioHeader = new AtomicReference<>();

        ExchangeFunction exchangeFunction = clientRequest -> {
            scenarioHeader.set(clientRequest.headers().getFirst(HttpHeaderConstants.MOCK_SCENARIO));
            return Mono.just(
                    ClientResponse.create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body("{\"percentage\":10}")
                            .build()
            );
        };

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        MockPercentageClient mockPercentageClient =
                new MockPercentageClient(webClient, "http://localhost:8081");

        StepVerifier.create(
                        mockPercentageClient.getPercentage()
                                .contextWrite(context -> context.put(HttpHeaderConstants.MOCK_SCENARIO, "retry-success"))
                )
                .assertNext(percentage -> assertEquals(new BigDecimal("10"), percentage))
                .verifyComplete();

        assertEquals("retry-success", scenarioHeader.get());
    }

    @Test
    void shouldIgnoreBlankMockScenarioHeaderFromReactiveContext() {
        AtomicReference<String> scenarioHeader = new AtomicReference<>();

        ExchangeFunction exchangeFunction = clientRequest -> {
            scenarioHeader.set(clientRequest.headers().getFirst(HttpHeaderConstants.MOCK_SCENARIO));
            return Mono.just(
                    ClientResponse.create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body("{\"percentage\":10}")
                            .build()
            );
        };

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        MockPercentageClient mockPercentageClient =
                new MockPercentageClient(webClient, "http://localhost:8081");

        StepVerifier.create(
                        mockPercentageClient.getPercentage()
                                .contextWrite(context -> context.put(HttpHeaderConstants.MOCK_SCENARIO, " "))
                )
                .assertNext(percentage -> assertEquals(new BigDecimal("10"), percentage))
                .verifyComplete();

        assertNull(scenarioHeader.get());
    }
}
