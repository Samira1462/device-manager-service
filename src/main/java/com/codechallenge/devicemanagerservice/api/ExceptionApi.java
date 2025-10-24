package com.codechallenge.devicemanagerservice.api;

import com.codechallenge.devicemanagerservice.exception.DeviceSaveException;
import com.codechallenge.devicemanagerservice.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionApi {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("details", ex.getDetails());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(DeviceSaveException.class)
    public ResponseEntity<Map<String, Object>> handleDeviceSaveException(DeviceSaveException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}