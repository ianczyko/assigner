package com.anczykowski.assigner.error;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> notFoundByIdException(EntityNotFoundException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MappingException.class)
    public ResponseEntity<?> mappingException(MappingException ex, WebRequest request) {
        // mapping error caused by entity not found exception (lazy loading from getReferenceById())
        if (ex.getCause() != null
                && ex.getCause().getCause() != null
                && ex.getCause().getCause().getCause() != null
                && ex.getCause().getCause().getCause() instanceof EntityNotFoundException enf
        ) {
            return notFoundByIdException(enf, request);
        }
        return defaultExceptionHandler(ex, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> unauthorizedException(UnauthorizedException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> unauthorizedException(AccessDeniedException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> defaultExceptionHandler(Exception ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
