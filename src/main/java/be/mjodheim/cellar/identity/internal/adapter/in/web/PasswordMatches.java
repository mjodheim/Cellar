package be.mjodheim.cellar.identity.internal.adapter.in.web;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Class-level Bean Validation constraint ensuring registration password confirmation matches.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
@interface PasswordMatches {

    /**
     * Validation message returned when passwords differ.
     *
     * @return validation message
     */
    String message() default "Passwords do not match";

    /**
     * Bean Validation groups.
     *
     * @return configured validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Bean Validation payload metadata.
     *
     * @return configured payload types
     */
    Class<? extends Payload>[] payload() default {};
}
