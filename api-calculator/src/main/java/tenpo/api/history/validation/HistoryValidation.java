package tenpo.api.history.validation;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import tenpo.api.common.exception.BadRequestException;
import tenpo.api.common.validation.RequestValidationSupport;
import tenpo.api.history.dto.HistoryApiRequest;

@Component
public class HistoryValidation {

    private final RequestValidationSupport requestValidationSupport;

    public HistoryValidation(Validator validator) {
        this.requestValidationSupport = new RequestValidationSupport(validator);
    }

    public Mono<HistoryApiRequest> validateQueryParams(ServerRequest request) {
        return Mono.fromSupplier(() -> {
            HistoryApiRequest historyApiRequest = new HistoryApiRequest();
            historyApiRequest.setPage(getIntOrDefault(request, "page", 0));
            historyApiRequest.setSize(getIntOrDefault(request, "size", 20));
            requestValidationSupport.validate(historyApiRequest, "Invalid history request");
            return historyApiRequest;
        });
    }

    private int getIntOrDefault(ServerRequest request, String name, int defaultValue) {
        return request.queryParam(name)
                .map(value -> parseInt(name, value))
                .orElse(defaultValue);
    }

    private int parseInt(String name, String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new BadRequestException("Invalid integer query param: " + name);
        }
    }
}
