package tenpo.api.common.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tenpo.api.common.validation.validator.RequiredDecimalValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredDecimalValidator.class)
public @interface RequiredDecimal {

    String message() default "{field} is required";

    String field();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
