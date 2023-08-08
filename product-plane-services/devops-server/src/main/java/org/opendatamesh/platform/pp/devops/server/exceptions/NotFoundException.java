package org.opendatamesh.platform.pp.devops.server.exceptions;

import org.opendatamesh.platform.pp.devops.api.resources.ODMDevOpsAPIStandardError;
import org.springframework.http.HttpStatus;

/**
 * Http Status: 404
 * 
 * The 404 (Not Found) status code indicates that the origin server did not find a 
 * current representation for the target resource or is not willing to disclose that 
 * one exists. A 404 status code does not indicate whether this lack of representation 
 * is temporary or permanent; the 410 (Gone) status code is preferred over 404 if 
 * the origin server knows, presumably through some configurable means, 
 * that the condition is likely to be permanent.
 * 
 * EXAMPLE: A request to get a data product that does not exist
 * 
 * @see https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found
 * @see ODMDevOpsAPIStandardError
 * @see OpenDataMeshAPIExceptionHandler
 */
public class NotFoundException extends OpenDataMeshAPIException {

	public NotFoundException(ODMDevOpsAPIStandardError error, String message) {
		super(error, message);
	}

	public NotFoundException() {
		super();
	}

	public NotFoundException(ODMDevOpsAPIStandardError error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(error, message, cause, enableSuppression, writableStackTrace);
	}


	public NotFoundException(ODMDevOpsAPIStandardError error, String message, Throwable cause) {
		super(error, message, cause);
	}


	public NotFoundException(Throwable cause) {
		super(cause);
	}


	@Override
	public HttpStatus getStatus() {
		return HttpStatus.NOT_FOUND;
	}

}