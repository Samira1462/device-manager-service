package com.codechallenge.devicemanagerservice.exception;
import java.util.Collection;

public class ValidationException extends RuntimeException {
    private final Collection<String> details;

    public ValidationException(Collection<String> errorMessages) {
        super("There are invalid values, please refer to the details!");
        this.details = errorMessages;
    }

    public Collection<String> getDetails() {
        return details;
    }
}