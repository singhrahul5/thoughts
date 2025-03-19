package me.rahul.thoughts.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import static me.rahul.thoughts.validation.PatternConstants.PASSWORD_PATTERN;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    private String message;

    @Override
    public void initialize(Password constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || !pattern.matcher(password).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }
}

