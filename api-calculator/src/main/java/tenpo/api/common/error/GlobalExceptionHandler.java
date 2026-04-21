package tenpo.api.common.error;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import tenpo.api.common.exception.BadRequestException;
import tenpo.domain.calculation.exception.PercentageUnavailableException;

@Slf4j
@Component
public class GlobalExceptionHandler {

    public Mono<ServerResponse> handle(Throwable throwable) {
        ModelError error = toModelError(throwable);
        HttpStatus status = HttpStatus.valueOf(error.status());

        if (status.is5xxServerError()) {
            log.error("Unhandled API error", throwable);
        } else {
            log.warn("Handled API error: {}", error.message());
        }

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    public HttpStatus resolveStatus(Throwable throwable) {
        return HttpStatus.valueOf(toModelError(throwable).status());
    }

    ModelError toModelError(Throwable throwable) {
        if (matches(throwable, BadRequestException.class)) {
            return new ModelError(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.name(),
                    throwable.getMessage(),
                    List.of()
            );
        }

        if (matches(throwable, PercentageUnavailableException.class)) {
            return new ModelError(
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    HttpStatus.SERVICE_UNAVAILABLE.name(),
                    throwable.getMessage(),
                    List.of()
            );
        }

        if (matches(throwable, RedisConnectionFailureException.class)
                || matches(throwable, RedisSystemException.class)) {
            return new ModelError(
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    HttpStatus.SERVICE_UNAVAILABLE.name(),
                    "Cache service temporarily unavailable",
                    List.of()
            );
        }

        if (matches(throwable, DataAccessException.class)) {
            return new ModelError(
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    HttpStatus.SERVICE_UNAVAILABLE.name(),
                    "Persistence service temporarily unavailable",
                    List.of()
            );
        }

        if (matches(throwable, WebClientException.class)) {
            return new ModelError(
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    HttpStatus.SERVICE_UNAVAILABLE.name(),
                    "External percentage service temporarily unavailable",
                    List.of()
            );
        }

        return new ModelError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Unexpected internal server error",
                List.of()
        );
    }

    private boolean matches(Throwable throwable, Class<? extends Throwable> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
