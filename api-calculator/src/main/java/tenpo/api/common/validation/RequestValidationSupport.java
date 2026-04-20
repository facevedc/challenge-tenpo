package tenpo.api.common.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Comparator;
import java.util.Set;
import tenpo.api.common.exception.BadRequestException;

public class RequestValidationSupport {

    private final Validator validator;

    public RequestValidationSupport(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T request, String defaultMessage) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .min(Comparator.naturalOrder())
                    .orElse(defaultMessage);
            throw new BadRequestException(message);
        }
    }
}
