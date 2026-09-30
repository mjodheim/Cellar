package be.mjodheim.cellar.identity.internal.adapter.in.web;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Bounds UTF-8 bytes, including multibyte passwords, to BCrypt's input limit. */
@Target({FIELD, METHOD, PARAMETER, RECORD_COMPONENT, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = Utf8SizeValidator.class)
public @interface Utf8Size {
    String message() default "must contain at most {max} UTF-8 bytes";
    int max() default 72;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
