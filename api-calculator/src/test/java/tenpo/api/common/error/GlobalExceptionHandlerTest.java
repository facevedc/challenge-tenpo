package tenpo.api.common.error;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tenpo.api.common.exception.BadRequestException;
import tenpo.domain.calculation.exception.PercentageUnavailableException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldBuildBadRequestErrorModel() {
        ModelError error = globalExceptionHandler.toModelError(new BadRequestException("num1 is required"));

        assertEquals(400, error.status());
        assertEquals("BAD_REQUEST", error.code());
        assertEquals("num1 is required", error.message());
    }

    @Test
    void shouldBuildServiceUnavailableErrorModelForPercentageUnavailable() {
        ModelError error = globalExceptionHandler.toModelError(
                new PercentageUnavailableException("Percentage service unavailable and no cached value found")
        );

        assertEquals(503, error.status());
        assertEquals("SERVICE_UNAVAILABLE", error.code());
        assertEquals("Percentage service unavailable and no cached value found", error.message());
    }

    @Test
    void shouldBuildServiceUnavailableErrorModelForCacheFailure() {
        ModelError error = globalExceptionHandler.toModelError(
                new RedisConnectionFailureException("redis down")
        );

        assertEquals(503, error.status());
        assertEquals("SERVICE_UNAVAILABLE", error.code());
        assertEquals("Cache service temporarily unavailable", error.message());
    }

    @Test
    void shouldBuildServiceUnavailableErrorModelForPersistenceFailure() {
        ModelError error = globalExceptionHandler.toModelError(
                new DataAccessResourceFailureException("postgres down")
        );

        assertEquals(503, error.status());
        assertEquals("SERVICE_UNAVAILABLE", error.code());
        assertEquals("Persistence service temporarily unavailable", error.message());
    }

    @Test
    void shouldBuildServiceUnavailableErrorModelForExternalServiceFailure() {
        ModelError error = globalExceptionHandler.toModelError(
                WebClientResponseException.create(503, "Service Unavailable", null, null, null)
        );

        assertEquals(503, error.status());
        assertEquals("SERVICE_UNAVAILABLE", error.code());
        assertEquals("External percentage service temporarily unavailable", error.message());
    }

    @Test
    void shouldHideUnexpectedInternalErrorMessage() {
        ModelError error = globalExceptionHandler.toModelError(new IOException("sensitive detail"));

        assertEquals(500, error.status());
        assertEquals("INTERNAL_SERVER_ERROR", error.code());
        assertEquals("Unexpected internal server error", error.message());
    }
}
