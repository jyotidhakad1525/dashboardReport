package com.automate.df.exception;

import org.springframework.http.HttpStatus;

public class LeadMgmtUploadException extends Exception{
	
	/**
	 * 
	 */
	
	
	
	
	private static final long serialVersionUID = 1L;
	private HttpStatus statusCode;

	public LeadMgmtUploadException(String msg) {
		super(msg);
	}
	
	public LeadMgmtUploadException(String msg,HttpStatus statusCode) {
		super(msg);
		this.statusCode=statusCode;
	}
	
	public LeadMgmtUploadException(String msg,Throwable t) {
		super(msg,t);
	}
	
	public HttpStatus getStatusCode() {
		return statusCode;
	}

}
