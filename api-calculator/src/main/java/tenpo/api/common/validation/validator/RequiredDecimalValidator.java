package tenpo.api.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import tenpo.api.common.validation.constraint.RequiredDecimal;

public class RequiredDecimalValidator implements ConstraintValidator<RequiredDecimal, BigDecimal> {

    private String field;

    @Override
    public void initialize(RequiredDecimal constraintAnnotation) {
        this.field = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value != null) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(field + " is required")
                .addConstraintViolation();
        return false;
    }
}
