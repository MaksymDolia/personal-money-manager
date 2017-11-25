package me.dolia.pmm.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Shows, that target field has to be validated as email, and it should be unique.
 *
 * @author Maksym Dolia
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {UniqueEmailValidator.class})
public @interface UniqueEmail {

  String message() default "This email already exists";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
