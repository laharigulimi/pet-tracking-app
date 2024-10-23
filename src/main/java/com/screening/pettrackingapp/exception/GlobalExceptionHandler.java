package com.screening.pettrackingapp.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PetValidationException.class)
    public ResponseEntity<String> handlePetValidationException(PetValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<String> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex,
                                                                      WebRequest request) {

        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        String propertyName = ex.getPropertyName();

        String message;
        if ("lostTracker".equals(propertyName) && path.endsWith("/dog")) {
            message = "The 'lostTracker' property is not allowed for Dog type. This property is only valid for Cat type.";
        } else {
            message = "Unknown property '" + propertyName + "' found in request";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
