package com.gsntalk.api.common.exception;

import com.gsntalk.api.util.GsntalkAPIResponse;

public class GsntalkAPIException extends Exception {

	private static final long serialVersionUID = -4954599821971467804L;

	private GsntalkAPIResponse response;
	private String failMsg;
	
	public GsntalkAPIException( GsntalkAPIResponse response ) {
		super( "GsntalkAPIException" );
		
		this.response = response;
	}
	
	public GsntalkAPIException( GsntalkAPIResponse response, String failMsg ) {
		super( "GsntalkAPIException" );
		
		this.response = response;
		this.failMsg = failMsg;
	}

	public GsntalkAPIResponse getResponse() {
		return response;
	}

	public String getFailMsg() {
		return failMsg;
	}
}