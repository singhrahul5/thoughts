package me.rahul.thoughts.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static me.rahul.thoughts.validation.PatternConstants.USERNAME_PATTERN;

@Constraint(validatedBy = {})
@NotNull(message = "Username cannot be null.")
@Pattern(regexp = USERNAME_PATTERN, message = "Username must match the pattern [a-z_][a-z_0-9]*")
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Username {

    String message() default "Invalid username format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}