package tenpo.infrastructure.client.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class MockPercentageClientTest {

    @Test
    void shouldReturnPercentageFromExternalMockService() {
        ExchangeFunction exchangeFunction = clientRequest -> Mono.just(
                ClientResponse.create(HttpStatus.OK)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("{\"percentage\":10}")
                        .build()
        );

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        MockPercentageClient mockPercentageClient =
                new MockPercentageClient(webClient, "http://localhost:8081", "/external/percentage");

        StepVerifier.create(mockPercentageClient.getPercentage())
                .assertNext(percentage -> assertEquals(new BigDecimal("10"), percentage))
                .verifyComplete();
    }
}
