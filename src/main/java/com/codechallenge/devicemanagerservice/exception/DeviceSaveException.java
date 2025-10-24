package com.codechallenge.devicemanagerservice.exception;

public class DeviceSaveException extends RuntimeException {
    public DeviceSaveException(String message) {
        super(message);
    }

    public DeviceSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
