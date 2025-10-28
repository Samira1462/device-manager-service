package com.codechallenge.devicemanagerservice.advices;

import com.codechallenge.devicemanagerservice.dto.ErrorResponseDto;
import com.codechallenge.devicemanagerservice.exception.DeviceNotFoundException;
import com.codechallenge.devicemanagerservice.exception.DeviceOperationException;
import com.codechallenge.devicemanagerservice.exception.DeviceUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                "INTERNAL_ERROR",
                "An unexpected error occurred.",
                Instant.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponseDto.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new ErrorResponseDto.FieldError(
                        err.getField(),
                        err.getRejectedValue(),
                        err.getDefaultMessage()
                ))
                .toList();

        ErrorResponseDto error = new ErrorResponseDto(
                "VALIDATION_ERROR",
                "Invalid input values",
                Instant.now(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(HttpMessageNotReadableException ex) {

        ErrorResponseDto error = new ErrorResponseDto(
                "VALIDATION_ERROR",
                ex.getMessage(),
                Instant.now(),
              null
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(DeviceNotFoundException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                "DEVICE_NOT_FOUND",
                ex.getMessage(),
                Instant.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DeviceUpdateException.class)
    public ResponseEntity<ErrorResponseDto> handleUpdate(DeviceUpdateException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                "DEVICE_UPDATE_ERROR",
                ex.getMessage(),
                Instant.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DeviceOperationException.class)
    public ResponseEntity<ErrorResponseDto> handleDeviceOperation(DeviceOperationException ex) {
        logger.warn("Device operation error: {}", ex.getMessage());
        var error = new ErrorResponseDto(
                "DEVICE_OPERATION_ERROR",
                ex.getMessage(),
                Instant.now(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                "VALIDATION_ERROR",
                ex.getMessage(),
                Instant.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error
        );
    }

}