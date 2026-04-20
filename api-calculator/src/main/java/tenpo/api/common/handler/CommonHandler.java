package tenpo.api.common.handler;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tenpo.api.common.error.GlobalExceptionHandler;

public class CommonHandler {

    private final GlobalExceptionHandler globalExceptionHandler;

    public CommonHandler() {
        this.globalExceptionHandler = new GlobalExceptionHandler();
    }

    protected Mono<ServerResponse> executeHandler(Mono<?> execution) {
        return execution
                .flatMap(ServerResponse.ok()::bodyValue)
                .onErrorResume(globalExceptionHandler::handle);
    }
}
