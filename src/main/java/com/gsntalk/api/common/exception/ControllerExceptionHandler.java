package com.gsntalk.api.common.exception;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gsntalk.api.common.extend.CommonController;

@ControllerAdvice
public class ControllerExceptionHandler extends CommonController {

	public ControllerExceptionHandler() {
		super( ControllerExceptionHandler.class );
	}
	
	@ResponseBody
	@ExceptionHandler( Exception.class )
	public JSONObject exceptionHandle( HttpServletRequest request, Exception e ) {
		this.LOGGER.error( "ControllerExceptionHandler.exceptionHandler [" + request.getRequestURI() + "]", e );
		
		return super.getServerErrorResponse( e.getMessage() );
	}
	
	@ResponseBody
	@ExceptionHandler( HttpMessageNotReadableException.class )
	public JSONObject exceptionHandle( HttpServletRequest request, HttpMessageNotReadableException e ) {
		return super.getServerErrorResponse( e.getMessage() );
	}
	
	@ResponseBody
	@ExceptionHandler( HttpMediaTypeNotSupportedException.class )
	public JSONObject exceptionHandle( HttpServletRequest request, HttpMediaTypeNotSupportedException e ) {
		return super.getServerErrorResponse( e.getMessage() );
	}
	
	@ResponseBody
	@ExceptionHandler( HttpRequestMethodNotSupportedException.class )
	public JSONObject exceptionHandle( HttpServletRequest request, HttpRequestMethodNotSupportedException e ) {
		return super.getServerErrorResponse( e.getMessage() );
	}
	
	@ResponseBody
	@ExceptionHandler( GsntalkAPIException.class )
	public JSONObject gsntalkExceptionHandle( HttpServletRequest request, GsntalkAPIException e ) {
		return super.getExceptionResponse( request.getRequestURI(), e.getResponse(), e.getFailMsg() );
	}
}
