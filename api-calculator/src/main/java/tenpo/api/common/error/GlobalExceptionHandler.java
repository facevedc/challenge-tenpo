package tenpo.api.common.error;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tenpo.api.common.exception.BadRequestException;
import tenpo.domain.calculation.exception.PercentageUnavailableException;

@Component
public class GlobalExceptionHandler {

    public Mono<ServerResponse> handle(Throwable throwable) {
        HttpStatus status = resolveStatus(throwable);

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ModelError(status.value(), status.name(), throwable.getMessage(), List.of()));
    }

    public HttpStatus resolveStatus(Throwable throwable) {
        return throwable instanceof BadRequestException
                ? HttpStatus.BAD_REQUEST
                : throwable instanceof PercentageUnavailableException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
