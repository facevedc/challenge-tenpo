package tenpo.api.calculation.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tenpo.api.calculation.mapper.CalculationDomainMapper;
import tenpo.api.calculation.mapper.CalculationResponseMapper;
import tenpo.api.calculation.validation.CalculationValidation;
import tenpo.api.common.handler.CommonHandler;
import tenpo.api.common.error.GlobalExceptionHandler;
import tenpo.domain.calculation.CalculationService;
import tenpo.domain.history.HistoryRegistrationService;
import tenpo.domain.history.model.HistoryLogCommand;
import tenpo.settings.HttpHeaderConstants;

@Component
public class CalculationHandler extends CommonHandler {

    private final CalculationService calculationService;
    private final CalculationValidation calculationValidation;
    private final CalculationDomainMapper calculationDomainMapper;
    private final CalculationResponseMapper calculationResponseMapper;
    private final GlobalExceptionHandler globalExceptionHandler;

    public CalculationHandler(
            CalculationService calculationService,
            CalculationValidation calculationValidation,
            CalculationDomainMapper calculationDomainMapper,
            CalculationResponseMapper calculationResponseMapper,
            GlobalExceptionHandler globalExceptionHandler,
            HistoryRegistrationService historyRegistrationService,
            ObjectMapper objectMapper
    ) {
        super(globalExceptionHandler);
        this.calculationService = calculationService;
        this.calculationValidation = calculationValidation;
        this.calculationDomainMapper = calculationDomainMapper;
        this.calculationResponseMapper = calculationResponseMapper;
        this.globalExceptionHandler = globalExceptionHandler;
        this.historyRegistrationService = historyRegistrationService;
        this.objectMapper = objectMapper;
    }

    private final HistoryRegistrationService historyRegistrationService;
    private final ObjectMapper objectMapper;

    public Mono<ServerResponse> calculate(ServerRequest request) {
        String mockScenario = request.headers().firstHeader(HttpHeaderConstants.MOCK_SCENARIO);
        OffsetDateTime startedAt = OffsetDateTime.now();
        long startedMillis = System.currentTimeMillis();
        String queryParams = toJson(request.queryParams().toSingleValueMap());

        Mono<ServerResponse> response = executeHandler(
                Mono.defer(() -> calculationValidation.validateQueryParams(request))
                        .map(calculationDomainMapper::toModel)
                        .flatMap(calculationService::calculate)
                        .map(calculationResponseMapper::toResponse)
                        .doOnSuccess(responseBody -> historyRegistrationService.register(
                                buildSuccessHistoryLog(request, startedAt, startedMillis, queryParams, responseBody)
                        ))
                        .doOnError(throwable -> historyRegistrationService.register(
                                buildErrorHistoryLog(request, startedAt, startedMillis, queryParams, throwable)
                        ))
        );

        if (mockScenario == null || mockScenario.isBlank()) {
            return response;
        }

        return response.contextWrite(context -> context.put(HttpHeaderConstants.MOCK_SCENARIO, mockScenario));
    }

    private HistoryLogCommand buildSuccessHistoryLog(ServerRequest request, OffsetDateTime startedAt,
                                                     long startedMillis, String queryParams, Object responseBody) {
        return new HistoryLogCommand(
                startedAt,
                request.path(),
                request.method().name(),
                queryParams,
                HttpStatus.OK.value(),
                toJson(responseBody),
                null,
                System.currentTimeMillis() - startedMillis
        );
    }

    private HistoryLogCommand buildErrorHistoryLog(
            ServerRequest request,
            OffsetDateTime startedAt,
            long startedMillis,
            String queryParams,
            Throwable throwable
    ) {
        return new HistoryLogCommand(
                startedAt,
                request.path(),
                request.method().name(),
                queryParams,
                globalExceptionHandler.resolveStatus(throwable).value(),
                null,
                throwable.getMessage(),
                System.currentTimeMillis() - startedMillis
        );
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            return String.valueOf(value);
        }
    }
}
