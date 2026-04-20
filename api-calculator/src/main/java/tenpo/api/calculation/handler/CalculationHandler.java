package tenpo.api.calculation.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tenpo.api.calculation.mapper.CalculationDomainMapper;
import tenpo.api.calculation.mapper.CalculationResponseMapper;
import tenpo.api.calculation.validation.CalculationValidation;
import tenpo.api.common.handler.CommonHandler;
import tenpo.domain.calculation.CalculationService;
import tenpo.settings.HttpHeaderConstants;

@Component
public class CalculationHandler extends CommonHandler {

    private final CalculationService calculationService;
    private final CalculationValidation calculationValidation;
    private final CalculationDomainMapper calculationDomainMapper;
    private final CalculationResponseMapper calculationResponseMapper;

    public CalculationHandler(
            CalculationService calculationService,
            CalculationValidation calculationValidation,
            CalculationDomainMapper calculationDomainMapper,
            CalculationResponseMapper calculationResponseMapper
    ) {
        this.calculationService = calculationService;
        this.calculationValidation = calculationValidation;
        this.calculationDomainMapper = calculationDomainMapper;
        this.calculationResponseMapper = calculationResponseMapper;
    }

    public Mono<ServerResponse> calculate(ServerRequest request) {
        String mockScenario = request.headers().firstHeader(HttpHeaderConstants.MOCK_SCENARIO);

        Mono<ServerResponse> response = executeHandler(
                Mono.defer(() -> calculationValidation.validateQueryParams(request))
                        .map(calculationDomainMapper::toModel)
                        .flatMap(calculationService::calculate)
                        .map(calculationResponseMapper::toResponse)
        );

        if (mockScenario == null || mockScenario.isBlank()) {
            return response;
        }

        return response.contextWrite(context -> context.put(HttpHeaderConstants.MOCK_SCENARIO, mockScenario));
    }
}
