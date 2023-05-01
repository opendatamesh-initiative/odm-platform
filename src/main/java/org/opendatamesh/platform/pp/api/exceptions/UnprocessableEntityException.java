package org.opendatamesh.platform.pp.api.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Http Status: 422
 * 
 * The 422 (Unprocessable Content) status code indicates that the server understands 
 * the content type of the request content (hence a 415 (Unsupported Media Type) 
 * status code is inappropriate), and the syntax of the request content is correct, 
 * but it was unable to process the contained instructions. For example, 
 * this status code can be sent if an XML request content contains well-formed 
 * (i.e., syntactically correct), but semantically erroneous XML instructions.
 * 
 * EXAMPLE: The provided descriptor respects the DPDS but violates some global 
 * policies enforced by the policyService 
 */
public class UnprocessableEntityException extends OpenDataMeshAPIException {

	public UnprocessableEntityException(OpenDataMeshAPIStandardError error, String message) {
		super(error, message);
	}

	
	public UnprocessableEntityException() {
		super();
	}


	public UnprocessableEntityException(OpenDataMeshAPIStandardError error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(error, message, cause, enableSuppression, writableStackTrace);
	}


	public UnprocessableEntityException(OpenDataMeshAPIStandardError error, String message, Throwable cause) {
		super(error, message, cause);
	}


	public UnprocessableEntityException(Throwable cause) {
		super(cause);
	}


	@Override
	public HttpStatus getStatus() {
		return HttpStatus.UNPROCESSABLE_ENTITY;
	}

}