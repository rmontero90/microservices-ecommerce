package com.ecommerce.products_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handlerResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {

        log.warn("Resource not found - Path: {}, Message: {}", request.getDescription(false), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

        problemDetail.setTitle("Resource Not Found");
        problemDetail.setProperty("Timestamp", Instant.now().toString());
        problemDetail.setType(URI.create("https://api-ecommerce.rmontero.me"));

        problemDetail.setProperty("Resource", ex.getResourceName());
        problemDetail.setProperty("Field", ex.getFieldName());
        problemDetail.setProperty("Value", ex.getFieldValue());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "The validation failed in one or more fields.");

        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("Timestamp", Instant.now().toString());
        problemDetail.setType(URI.create("https://api-ecommerce.rmontero.me/validation-error"));

        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((fieldError) -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        problemDetail.setProperty("FieldErrors", errorMap);

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handlerException(Exception ex, WebRequest request) {

        log.warn("Unexpected error occurred {}: Message: {}", request.getDescription(false), ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error occurred. Please contact support.");

        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("Timestamp", Instant.now().toString());
        problemDetail.setType(URI.create("https://api-ecommerce.rmontero.me/internal-server-error"));

        return problemDetail;
    }
}
