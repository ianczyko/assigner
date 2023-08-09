package com.anczykowski.assigner.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFoundException(NotFoundException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> unauthorizedException(UnauthorizedException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> defaultExceptionHandler(Exception ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
