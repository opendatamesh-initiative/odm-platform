package org.opendatamesh.platform.up.policy.server.api.v1.controllers;

import org.opendatamesh.platform.up.policy.api.v1.resources.ErrorResource;
import org.opendatamesh.platform.up.policy.server.exceptions.PolicyserviceOpaAPIException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class PolicyserviceOpaAPIExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({PolicyserviceOpaAPIException.class})
	protected ResponseEntity<Object> handleOpenDataMeshException(PolicyserviceOpaAPIException e, WebRequest request) {
		String errorLogMessage = e.getErrorName() + ":" + e.getMessage();
		errorLogMessage += e.getCause()!=null?" : " + e.getCause().getMessage():"";
		logger.error(errorLogMessage);
		String url = getUrl(request);
		ErrorResource error = new ErrorResource(
				e.getStatus().value(),
				e.getStandardErrorCode(),
				e.getStandardErrorDescription(),
				e.getMessage(),
				url
		);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return handleExceptionInternal(e, error, headers, e.getStatus(), request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		if(!PolicyserviceOpaAPIException.class.isAssignableFrom(e.getClass())) {
			String errorLogMessage = e.getClass().getName() + ":" + e.getMessage();
			errorLogMessage += e.getCause()!=null?" : " + e.getCause().getMessage():"";
			logger.error(errorLogMessage);
		}

		if (body == null && !HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			logger.debug("Creating body for unhandled exception", e);
			headers.setContentType(MediaType.APPLICATION_JSON);
			String url = getUrl(request);
			String message = e.getMessage();
			body = new ErrorResource(status.value(), "50000", e.getClass().getName(), message, url);
		}
		return super.handleExceptionInternal(e, body, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<ObjectError> errors = ex.getAllErrors();
		String message = String.format("Errors: %s", errors.stream().map(Objects::toString).collect(Collectors.joining("; ")));
		ErrorResource errorResource = new ErrorResource(
				status.value(),
				"50000",
				"Bind Exception",
				message,
				getUrl(request));
		return handleExceptionInternal(ex, errorResource, headers, status, request);
	}
	private String getUrl(WebRequest request) {
		String url = request.toString();
		if (request instanceof ServletWebRequest) {
			ServletWebRequest r = (ServletWebRequest) request;
			url = r.getRequest().getRequestURI();
		}
		return url;
	}
}