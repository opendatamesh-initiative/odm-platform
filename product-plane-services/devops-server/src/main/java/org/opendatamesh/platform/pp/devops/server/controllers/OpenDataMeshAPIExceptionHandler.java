package org.opendatamesh.platform.pp.devops.server.controllers;


import org.apache.commons.lang.exception.ExceptionUtils;
import org.opendatamesh.platform.pp.devops.api.resources.ErrorRes;
import org.opendatamesh.platform.pp.devops.api.resources.ODMDevOpsAPIStandardError;
import org.opendatamesh.platform.pp.devops.server.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.devops.server.exceptions.OpenDataMeshAPIException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class OpenDataMeshAPIExceptionHandler extends ResponseEntityExceptionHandler {


	@ExceptionHandler({OpenDataMeshAPIException.class})
	protected ResponseEntity<Object> handleOpenDataMeshException(OpenDataMeshAPIException e, WebRequest request) {
		String errorLogMessage = e.getErrorName() + ":" + e.getMessage();
		Throwable rootCause = ExceptionUtils.getRootCause(e);
		String rootCauseMessage = rootCause!=null? " : " + rootCause.getMessage(): "";
		errorLogMessage += rootCauseMessage;
		logger.error(errorLogMessage, e);
		String url = getUrl(request);
		ErrorRes error = new ErrorRes(e.getStatus().value(), e.getStandardError(), e.getMessage(), url);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return handleExceptionInternal(e, error, headers, e.getStatus(), request);
	}

	@ExceptionHandler({PropertyReferenceException.class})
	protected ResponseEntity<Object> handlePropertyReferenceException(PropertyReferenceException e, WebRequest request) {
		BadRequestException badRequestException = new BadRequestException(ODMDevOpsAPIStandardError.SC400_99_PROPERTY_REF_EXCEPTION, e.getMessage(), e);
		return handleOpenDataMeshException(badRequestException, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		if(!OpenDataMeshAPIException.class.isAssignableFrom(e.getClass())) {
			String errorLogMessage = e.getClass().getName() + ":" + e.getMessage();
			Throwable rootCause = ExceptionUtils.getRootCause(e);
			errorLogMessage += rootCause!=null?" : " + rootCause.getMessage():"";
			logger.error(errorLogMessage);
		}
		
		if(HttpMessageNotReadableException.class.isAssignableFrom(e.getClass())) { // if something went erong while parsing the request body...
			headers.setContentType(MediaType.APPLICATION_JSON);
			String url = getUrl(request);
			String message = e.getMessage();
			body = new ErrorRes(
				status.value(), 
				ODMDevOpsAPIStandardError.SC400_00_REQUEST_BODY_IS_NOT_READABLE, 
				message, url);
		} else if(HttpMediaTypeNotSupportedException.class.isAssignableFrom(e.getClass())) { // if request media type is not supported...
			headers.setContentType(MediaType.APPLICATION_JSON);
			String url = getUrl(request);
			String message = e.getMessage();
			body = new ErrorRes(
				status.value(), 
				ODMDevOpsAPIStandardError.SC415_01_REQUEST_MEDIA_TYPE_NOT_SUPPORTED, 
				message, url);
		} else if(HttpMediaTypeNotAcceptableException.class.isAssignableFrom(e.getClass())) { // if request media type is not supported...
			headers.setContentType(MediaType.APPLICATION_JSON);
			String url = getUrl(request);
			String message = e.getMessage();
			body = new ErrorRes(
				status.value(), 
				ODMDevOpsAPIStandardError.SC406_01_REQUEST_ACCEPTED_MEDIA_TYPES_NOT_SUPPORTED, 
				message, url);
		} else if (body == null && !HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) { // if it is an unexpected internal exception...
			headers.setContentType(MediaType.APPLICATION_JSON);
			String url = getUrl(request);
			String message = e.getMessage();
			body = new ErrorRes(status.value(), "50000", e.getClass().getName(), message, url);
		}
		return super.handleExceptionInternal(e, body, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<ObjectError> errors = ex.getAllErrors();
		String message = String.format("Errors: %s", errors.stream().map(Objects::toString).collect(Collectors.joining("; ")));
		ErrorRes errorRes = new ErrorRes(
				status.value(),
				"50000",
				"Bind Exception",
				message,
				getUrl(request));
		return handleExceptionInternal(ex, errorRes, headers, status, request);
	}

	@ExceptionHandler({RuntimeException.class})
	protected ResponseEntity<Object> handleRuntimeException(RuntimeException e, WebRequest request) {
		logger.error("Unknown server error: ", e);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String url = getUrl(request);
		ErrorRes errorRes = new ErrorRes(status.value(), null, "Server Error",
				"Unknown Internal Server Error", url);
		return handleExceptionInternal(e, errorRes, headers, status, request);
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