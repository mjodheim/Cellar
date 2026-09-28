package be.mjodheim.cellar.identity.internal.adapter.in.web;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

/**
 * Validates password confirmation without leaking password values.
 */
class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterRequest> {

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
