package tenpo.api.common.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tenpo.api.common.validation.validator.ValidPageValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPageValidator.class)
public @interface ValidPage {

    String message() default "page must be greater than or equal to 0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
