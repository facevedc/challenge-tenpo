package tenpo.infrastructure.client.mock;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tenpo.domain.calculation.PercentageProvider;

@Component
public class MockPercentageClient implements PercentageProvider {

    private final WebClient webClient;
    private final String baseUrl;
    private final String percentagePath;

    public MockPercentageClient(
            WebClient webClient,
            @Value("${tenpo.client.percentage.base-url}") String baseUrl,
            @Value("${tenpo.client.percentage.path}") String percentagePath
    ) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.percentagePath = percentagePath;
    }

    @Override
    public Mono<BigDecimal> getPercentage() {
        return webClient
                .get()
                .uri(baseUrl + percentagePath)
                .retrieve()
                .bodyToMono(ExternalPercentageResponse.class)
                .map(ExternalPercentageResponse::percentage);
    }
}
