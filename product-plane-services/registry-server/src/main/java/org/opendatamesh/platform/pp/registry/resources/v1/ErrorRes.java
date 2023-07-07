package org.opendatamesh.platform.pp.registry.resources.v1;

import lombok.Data;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;

import java.util.Date;

@Data
public class ErrorRes {
	
	// HTTP Status code
	int status;

	// Standard error code
	String code;

	// Standard error description
	String description;

	// Exception message. 
	// Do not include exception cause's message. 
	// It is appended only to the log error message. 
	String message;

	// Service endpoint
	String path;

	// Error timestamp
	long timestamp = new Date().getTime();

	public ErrorRes() {

	}

	public ErrorRes(int status, OpenDataMeshAPIStandardError error, String message, String path) {
		super();
		this.status = status;
		this.code = error.code();
		this.description = error.description();
		this.message = message;
		this.path = path;
	}	

	public ErrorRes(int status, String errorCode, String errorDescription,  String message, String path) {
		super();
		this.status = status;
		this.code = errorCode;
		this.description = errorDescription;
		this.message = message;
		this.path = path;
	}	
}