package com.anczykowski.assigner.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.modelmapper.MappingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
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

    @ExceptionHandler(MalformedCsvException.class)
    public ResponseEntity<?> malformedCsvException(MalformedCsvException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> notFoundByIdException(EntityNotFoundException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity<?> notFoundJpaException(JpaObjectRetrievalFailureException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        if (ex.getCause() != null
                && ex.getCause().getCause() != null
        ) {
            errorResponseEntity = new ErrorResponseEntity(ex.getCause().getCause().getMessage(), request.getDescription(false));
        }
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> forbiddenException(ForbiddenException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> forbiddenException(AccessDeniedException ex, WebRequest request) {
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> defaultExceptionHandler(Exception ex, WebRequest request) {
        logger.error("defaultExceptionHandler", ex);
        var errorResponseEntity = new ErrorResponseEntity(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
