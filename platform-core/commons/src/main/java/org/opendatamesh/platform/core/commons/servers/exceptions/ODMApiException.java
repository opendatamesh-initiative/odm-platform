package org.opendatamesh.platform.core.commons.servers.exceptions;

import org.springframework.http.HttpStatus;

public abstract class ODMApiException extends RuntimeException{

	ODMApiStandardErrors error;

	public ODMApiException() {
		super();
	}

	public ODMApiException(ODMApiStandardErrors error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		setError(error);
	}

	public ODMApiException(ODMApiStandardErrors error, String message,Throwable cause) {
		super(message, cause);
		setError(error);
	}

	public ODMApiException(ODMApiStandardErrors error, String message) {
		super(message);
		setError(error);
	}

	public ODMApiException(Throwable cause) {
		super(cause);
	}

	public void setError(ODMApiStandardErrors error) {
		this.error = error;
	}

	/**
	 * @return the error
	 */
	public ODMApiStandardErrors getStandardError() {
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