package com.codechallenge.devicemanagerservice.exception;

public class DeviceOperationException extends RuntimeException {
    public DeviceOperationException(String message) {
        super(message);
    }
}