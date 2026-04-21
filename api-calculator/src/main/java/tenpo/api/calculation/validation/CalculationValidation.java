package tenpo.api.calculation.validation;

import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import tenpo.api.calculation.dto.CalculationApiRequest;
import tenpo.api.common.validation.RequestValidationSupport;
import tenpo.api.common.exception.BadRequestException;

@Component
public class CalculationValidation {

    private final RequestValidationSupport requestValidationSupport;

    public CalculationValidation(Validator validator) {
        this.requestValidationSupport = new RequestValidationSupport(validator);
    }

    public Mono<CalculationApiRequest> validateQueryParams(ServerRequest request) {
        return Mono.fromSupplier(() -> {
            CalculationApiRequest calculationApiRequest = new CalculationApiRequest();
            calculationApiRequest.setNum1(getOptionalDecimal(request, "num1"));
            calculationApiRequest.setNum2(getOptionalDecimal(request, "num2"));
            requestValidationSupport.validate(calculationApiRequest, "Invalid calculation request");
            return calculationApiRequest;
        });
    }

    private BigDecimal getOptionalDecimal(ServerRequest request, String name) {
        return request.queryParam(name)
                .map(value -> parseDecimal(name, value))
                .orElse(null);
    }

    private BigDecimal parseDecimal(String name, String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            throw new BadRequestException("Invalid decimal query param: " + name);
        }
    }
}
