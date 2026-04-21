package tenpo.api.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tenpo.api.common.validation.constraint.ValidSize;

public class ValidSizeValidator implements ConstraintValidator<ValidSize, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && value > 0;
    }
}
