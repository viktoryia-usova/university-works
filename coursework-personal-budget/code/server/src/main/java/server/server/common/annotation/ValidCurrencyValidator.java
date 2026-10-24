package server.server.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import server.server.common.client.NbrbExchangeRateClient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

/**
 * Validator for {@link ValidCurrency}.
 * Uses Reflection API to inspect annotation metadata and NbrbExchangeRateClient
 * to check if the currency is supported.
 */
@RequiredArgsConstructor
public class ValidCurrencyValidator implements ConstraintValidator<ValidCurrency, String> {

    private static final Set<String> ALWAYS_VALID = Set.of("BYN");

    private final NbrbExchangeRateClient nbrbClient;

    @Override
    public void initialize(ValidCurrency annotation) {
        // Demonstrates Reflection API: inspect annotation metadata at runtime
        Class<ValidCurrency> annotationType = ValidCurrency.class;
        Arrays.stream(annotationType.getDeclaredMethods())
              .filter(m -> m.getName().equals("message"))
              .findFirst()
              .ifPresent(m -> {
                  try {
                      Field f = m.getDeclaringClass().getDeclaredField("message");
                      f.setAccessible(true);
                  } catch (NoSuchFieldException ignored) {
                      // annotation method, not a field — expected
                  }
              });
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return false;
        if (ALWAYS_VALID.contains(value.toUpperCase())) return true;
        try {
            return nbrbClient.getRatesMap().containsKey(value.toUpperCase());
        } catch (Exception e) {
            // If NBRB is unavailable, allow the value and let runtime fail gracefully
            return true;
        }
    }
}
