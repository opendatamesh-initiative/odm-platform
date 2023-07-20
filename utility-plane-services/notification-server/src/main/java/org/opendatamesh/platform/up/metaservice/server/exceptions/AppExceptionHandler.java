package org.opendatamesh.platform.up.metaservice.server.exceptions;

import org.opendatamesh.platform.up.notification.api.resources.ErrorResource;
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
        List<ErrorResource> errorResources = ex.getConstraintViolations()
                .stream()
                .map(constraintViolation -> new ErrorResource(ex.getClass().getSimpleName(), constraintViolation.getMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(errorResources, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    protected ResponseEntity<Object> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        logger.error(ex.getMessage());
        List<ErrorResource> errorResources = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> new ErrorResource(error.getClass().getSimpleName(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(errorResources, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity<Object> handleAll(
            Exception ex,
            WebRequest request
    ) {
        logger.error(ex.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ErrorResource errorResource = new ErrorResource(
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );
        return new ResponseEntity<>(Arrays.asList(errorResource), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}