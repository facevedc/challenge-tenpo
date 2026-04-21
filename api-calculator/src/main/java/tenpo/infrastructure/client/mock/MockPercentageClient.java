package tenpo.infrastructure.client.mock;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tenpo.domain.calculation.ExternalPercentageProvider;
import tenpo.settings.HttpHeaderConstants;

@Component
public class MockPercentageClient implements ExternalPercentageProvider {

    private final WebClient webClient;
    private final String baseUrl;
    private static final String PERCENTAGE_PATH = "/external/percentage";

    public MockPercentageClient(
            WebClient webClient,
            @Value("${tenpo.client.percentage.base-url}") String baseUrl
    ) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    @Override
    public Mono<BigDecimal> getPercentage() {
        return Mono.deferContextual(contextView -> {
            WebClient.RequestHeadersSpec<?> requestSpec = webClient
                    .get()
                    .uri(baseUrl + PERCENTAGE_PATH);

            MockScenarioContext.getScenario(contextView)
                    .ifPresent(mockScenario -> requestSpec.header(HttpHeaderConstants.MOCK_SCENARIO, mockScenario));

            return requestSpec
                    .retrieve()
                    .bodyToMono(ExternalPercentageResponse.class)
                    .map(ExternalPercentageResponse::percentage);
        });
    }
}
