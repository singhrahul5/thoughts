package me.rahul.thoughts.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.AbstractEmailValidator;

import java.util.regex.Pattern;

import static me.rahul.thoughts.validation.PatternConstants.PASSWORD_PATTERN;
import static me.rahul.thoughts.validation.PatternConstants.USERNAME_PATTERN;

public class UsernameOrEmailValidator extends AbstractEmailValidator<UsernameOrEmail> {

    private static final Pattern pattern = Pattern.compile(USERNAME_PATTERN);
    private String message;

    @Override
    public void initialize(UsernameOrEmail constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null || !(super.isValid(value, context) || pattern.matcher(value).matches())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }
}
