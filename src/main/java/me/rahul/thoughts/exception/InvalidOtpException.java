package me.rahul.thoughts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;

public class InvalidOtpException extends CustomException  {
    private final HttpStatusCode statusCode = HttpStatus.UNAUTHORIZED;

    public InvalidOtpException(String message) {
        super(message);
    }

    @Override
    @NonNull
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    @NonNull
    public ProblemDetail getBody() {
        return ProblemDetail.forStatusAndDetail(statusCode, getMessage());
    }
}
