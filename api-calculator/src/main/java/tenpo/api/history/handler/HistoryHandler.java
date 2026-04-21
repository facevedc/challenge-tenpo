package tenpo.api.history.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tenpo.api.common.error.GlobalExceptionHandler;
import tenpo.api.history.mapper.HistoryDomainMapper;
import tenpo.api.history.mapper.HistoryResponseMapper;
import tenpo.api.history.validation.HistoryValidation;
import tenpo.domain.history.HistoryQueryService;

@Component
public class HistoryHandler {

    private final HistoryQueryService historyQueryService;
    private final HistoryValidation historyValidation;
    private final HistoryDomainMapper historyDomainMapper;
    private final HistoryResponseMapper historyResponseMapper;
    private final GlobalExceptionHandler globalExceptionHandler;

    public HistoryHandler(
            HistoryQueryService historyQueryService,
            HistoryValidation historyValidation,
            HistoryDomainMapper historyDomainMapper,
            HistoryResponseMapper historyResponseMapper,
            GlobalExceptionHandler globalExceptionHandler
    ) {
        this.historyQueryService = historyQueryService;
        this.historyValidation = historyValidation;
        this.historyDomainMapper = historyDomainMapper;
        this.historyResponseMapper = historyResponseMapper;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    public Mono<ServerResponse> getHistory(ServerRequest request) {
        return Mono.defer(() -> historyValidation.validateQueryParams(request))
                .map(historyDomainMapper::toModel)
                .flatMap(historyQueryService::findHistory)
                .map(historyResponseMapper::toResponse)
                .flatMap(ServerResponse.ok()::bodyValue)
                .onErrorResume(globalExceptionHandler::handle);
    }
}
