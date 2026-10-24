package server.server.common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom annotation that validates a currency code is supported by the NBRB API.
 * Demonstrates use of custom annotations + Reflection API.
 */
@Documented
@Constraint(validatedBy = ValidCurrencyValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCurrency {
    String message() default "Unsupported currency code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
