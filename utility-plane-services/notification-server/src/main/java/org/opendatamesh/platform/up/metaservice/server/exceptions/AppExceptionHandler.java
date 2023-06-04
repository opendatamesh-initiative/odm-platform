package org.opendatamesh.platform.up.metaservice.server.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class AppExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler({ ConstraintViolationException.class })
    protected ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException ex,
            WebRequest request) {
        logger.error(ex.getMessage());
        List<Error> errors = ex.getConstraintViolations()
                .stream()
                .map(constraintViolation -> new Error(ex.getClass().getSimpleName(), constraintViolation.getMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    protected ResponseEntity<Object> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        logger.error(ex.getMessage());
        List<Error> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> new Error(error.getClass().getSimpleName(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity<Object> handleAll(
            Exception ex,
            WebRequest request
    ) {
        logger.error(ex.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Error error = new Error(
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );
        return new ResponseEntity<>(Arrays.asList(error), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}