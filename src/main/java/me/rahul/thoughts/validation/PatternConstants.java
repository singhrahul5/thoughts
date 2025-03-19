package me.rahul.thoughts.validation;

public class PatternConstants {

    public static final String OTP_PATTERN = "^\\d{6}$";

    public static final String PASSWORD_PATTERN = "^\\p{Graph}{8,100}$";

    public static final String USERNAME_PATTERN = "^[a-z_][a-z_0-9]{1,29}$";
}
