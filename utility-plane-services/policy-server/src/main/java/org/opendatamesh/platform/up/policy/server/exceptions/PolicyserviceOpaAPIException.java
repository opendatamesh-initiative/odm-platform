package org.opendatamesh.platform.up.policy.server.exceptions;

import org.opendatamesh.platform.up.policy.api.v1.errors.PolicyserviceOpaAPIStandardError;
import org.springframework.http.HttpStatus;

public abstract class PolicyserviceOpaAPIException extends RuntimeException{

	PolicyserviceOpaAPIStandardError error;

	public PolicyserviceOpaAPIException() {
		super();
	}

	public PolicyserviceOpaAPIException(PolicyserviceOpaAPIStandardError error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		setError(error);
	}

	public PolicyserviceOpaAPIException(PolicyserviceOpaAPIStandardError error, String message, Throwable cause) {
		super(message, cause);
		setError(error);
	}

	public PolicyserviceOpaAPIException(PolicyserviceOpaAPIStandardError error, String message) {
		super(message);
		setError(error);
	}

	public PolicyserviceOpaAPIException(Throwable cause) {
		super(cause);
	}

	public void setError(PolicyserviceOpaAPIStandardError error) {
		this.error = error;
	}

	/**
	 * @return the error
	 */
	public PolicyserviceOpaAPIStandardError getStandardError() {
		return error;
	}

	/**
	 * @return the error code
	 */
	public String getStandardErrorCode() {
		return error!=null?error.code():null;		
	}

	/**
	 * @return the error description
	 */
	public String getStandardErrorDescription() {
		return error!=null?error.description():null;	
	}

	

	/**
	 * @return the errorName
	 */
	public String getErrorName() {
		return getClass().getSimpleName();	
	}

	/**
	 * @return the status
	 */
	public abstract HttpStatus getStatus();	
	

}