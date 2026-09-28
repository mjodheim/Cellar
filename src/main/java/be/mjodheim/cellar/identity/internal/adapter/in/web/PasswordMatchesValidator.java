package be.mjodheim.cellar.identity.internal.adapter.in.web;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

/**
 * Validates password confirmation without logging or exposing password values.
 */
class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterRequest> {

    /**
     * Compares the password and confirmation fields and attaches the violation to
     * {@code passwordConfirm} when they differ.
     *
     * @param value registration payload to validate
     * @param context Bean Validation context
     * @return {@code true} when the values match or the request itself is null
     */
    @Override
    public boolean isValid(RegisterRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean valid = Objects.equals(value.password(), value.passwordConfirm());
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords do not match")
                    .addPropertyNode("passwordConfirm")
                    .addConstraintViolation();
        }
        return valid;
    }
}
