package tenpo.api.history;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.test.StepVerifier;
import tenpo.api.common.exception.BadRequestException;
import tenpo.api.history.validation.HistoryValidation;

class HistoryValidationTest {

    private final HistoryValidation historyValidation =
            new HistoryValidation(Validation.buildDefaultValidatorFactory().getValidator());

    @Test
    void shouldUseDefaultHistoryPaginationValues() {
        MockServerRequest request = MockServerRequest.builder().build();

        StepVerifier.create(historyValidation.validateQueryParams(request))
                .assertNext(result -> {
                    assertEquals(0, result.getPage());
                    assertEquals(20, result.getSize());
                })
                .verifyComplete();
    }

    @Test
    void shouldFailWhenHistoryPaginationIsInvalid() {
        MockServerRequest request = MockServerRequest.builder()
                .queryParam("page", "invalid")
                .queryParam("size", "20")
                .build();

        StepVerifier.create(historyValidation.validateQueryParams(request))
                .expectErrorSatisfies(error -> assertEquals(
                        "Invalid integer query param: page",
                        ((BadRequestException) error).getMessage()
                ))
                .verify();
    }

    @Test
    void shouldFailWhenHistoryPageIsNegative() {
        MockServerRequest request = MockServerRequest.builder()
                .queryParam("page", "-1")
                .queryParam("size", "20")
                .build();

        StepVerifier.create(historyValidation.validateQueryParams(request))
                .expectErrorSatisfies(error -> assertEquals(
                        "page must be greater than or equal to 0",
                        ((BadRequestException) error).getMessage()
                ))
                .verify();
    }
}
