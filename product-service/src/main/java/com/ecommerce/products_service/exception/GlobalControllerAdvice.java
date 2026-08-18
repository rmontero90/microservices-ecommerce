package com.ecommerce.products_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handlerResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

        problemDetail.setTitle("Resource Not Found");
        problemDetail.setProperty("Timestamp", Instant.now().toString());
        problemDetail.setType(URI.create("https://api-ecommerce.rmontero.me"));

        problemDetail.setProperty("Resource", ex.getResourceName());
        problemDetail.setProperty("Field", ex.getFieldName());
        problemDetail.setProperty("Value", ex.getFieldValue());

        return problemDetail;
    }
}
