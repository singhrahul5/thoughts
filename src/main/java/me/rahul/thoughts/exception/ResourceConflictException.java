package me.rahul.thoughts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;

public class ResourceConflictException extends CustomException  {

    private final static HttpStatusCode statusCode = HttpStatus.CONFLICT;

    public ResourceConflictException(String message) {
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
