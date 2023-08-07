package org.opendatamesh.platform.pp.registry.api.v1.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Http Status: 409
 * 
 * The 409 (Conflict) status code indicates that the request could not be completed due 
 * to a conflict with the current state of the target resource. This code is used in 
 * situations where the user might be able to resolve the conflict and resubmit the request. 
 * The server SHOULD generate content that includes enough information for a user to 
 * recognize the source of the conflict.

 * Conflicts are most likely to occur in response to a PUT request. For example, 
 * if versioning were being used and the representation being PUT included changes to a 
 * resource that conflict with those made by an earlier (third-party) request, the origin 
 * server might use a 409 response to indicate that it can't complete the request. 
 * In this case, the response representation would likely contain information useful for 
 * merging the differences based on the revision history.
 * 
 * EXAMPLE A data product deploy request occuring while another deploy request is already running
 * on the same product.
 * 
 * @see https://www.rfc-editor.org/rfc/rfc9110.html#name-409-conflict
 * @see OpenDataMeshAPIStandardError
 * @see OpenDataMeshAPIExceptionHandler
 */
public class ConflictException extends OpenDataMeshAPIException {

    public ConflictException(OpenDataMeshAPIStandardError error, String message) {
		super(error, message);
	}

	
	public ConflictException() {
		super();
	}


	public ConflictException(OpenDataMeshAPIStandardError error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(error, message, cause, enableSuppression, writableStackTrace);
	}


	public ConflictException(OpenDataMeshAPIStandardError error, String message, Throwable cause) {
		super(error, message,  cause);
	}


	public ConflictException(Throwable cause) {
		super(cause);
	}


	@Override
	public HttpStatus getStatus() {
		return HttpStatus.CONFLICT;
	}
}
