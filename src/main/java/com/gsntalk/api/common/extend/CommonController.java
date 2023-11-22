package com.gsntalk.api.common.extend;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;

public class CommonController {

	protected Logger LOGGER;
	
	public CommonController( Class<?> c ) {
		this.LOGGER = LoggerFactory.getLogger( c );
	}
	
	/**
	 * Request-body JSONObject 재구성
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	protected JSONObject resetJSONObject( JSONObject param ) throws Exception {
		return (JSONObject)new JSONParser().parse( param.toJSONString() );
	}
	
	@SuppressWarnings( "unchecked" )
	protected JSONObject getSuccessResponse() {
		JSONObject rtnItem = new JSONObject();
		rtnItem.put( "resCode", GsntalkAPIResponse.SUCCESS.getResCode() );
		
		return rtnItem;
	}
	
	@SuppressWarnings( "unchecked" )
	protected JSONObject getSuccessResponse( HttpServletResponse response, String jwtToken ) {
		JSONObject rtnItem = new JSONObject();
		rtnItem.put( "resCode", GsntalkAPIResponse.SUCCESS.getResCode() );
		
		// set token to response
		response.addHeader( GsntalkConstants.GSN_TALK_TOKEN_KEY, jwtToken );
		
		return rtnItem;
	}
	
	@SuppressWarnings( "unchecked" )
	protected JSONObject getServerErrorResponse() {
		JSONObject rtnItem = new JSONObject();
		rtnItem.put( "resCode", GsntalkAPIResponse.SERVER_ERROR.getResCode() );
		
		return rtnItem;
	}
	
	@SuppressWarnings( "unchecked" )
	protected JSONObject getServerErrorResponse( String errMsg ) {
		JSONObject rtnItem = new JSONObject();
		rtnItem.put( "resCode", GsntalkAPIResponse.SERVER_ERROR.getResCode() );
		rtnItem.put( "errMsg", errMsg );
		
		return rtnItem;
	}
	
	@SuppressWarnings( "unchecked" )
	protected JSONObject getExceptionResponse( String requestUri, GsntalkAPIResponse response, String failMsg ) {
		JSONObject rtnItem = new JSONObject();
		rtnItem.put( "resCode", response.getResCode() );
		if( !GsntalkUtil.isEmpty( failMsg ) ) {
			rtnItem.put( "failMsg", failMsg );
		}
		
		this.LOGGER.info( ">>>>>>>>>>> getExceptionResponse [" + requestUri + "] : " + rtnItem.toJSONString() );
		
		return rtnItem;
	}
	
	@SuppressWarnings( "unchecked" )
	protected JSONObject getItemResponse( JSONObject item ) {
		JSONObject rtnItem = new JSONObject();
		rtnItem.put( "resCode", GsntalkAPIResponse.SUCCESS.getResCode() );
		rtnItem.put( "item", item );
		
		return rtnItem;
	}
	
	@SuppressWarnings( "unchecked" )
	protected JSONObject getItemResponse( HttpServletResponse response, String jwtToken, JSONObject item ) {
		JSONObject rtnItem = new JSONObject();
		rtnItem.put( "resCode", GsntalkAPIResponse.SUCCESS.getResCode() );
		rtnItem.put( "item", item );
		
		// set token to response
		response.addHeader( GsntalkConstants.GSN_TALK_TOKEN_KEY, jwtToken );
		
		return rtnItem;
	}
	
	@SuppressWarnings( "unchecked" )
	protected JSONObject getItemsResponse( JSONArray items ) {
		JSONObject rtnItem = new JSONObject();
		rtnItem.put( "resCode", GsntalkAPIResponse.SUCCESS.getResCode() );
		rtnItem.put( "items", items );
		
		return rtnItem;
	}
	
	@SuppressWarnings( "unchecked" )
	protected JSONObject getItemsResponse( HttpServletResponse response, String jwtToken, JSONArray items ) {
		JSONObject rtnItem = new JSONObject();
		rtnItem.put( "resCode", GsntalkAPIResponse.SUCCESS.getResCode() );
		rtnItem.put( "items", items );
		
		// set token to response
		response.addHeader( GsntalkConstants.GSN_TALK_TOKEN_KEY, jwtToken );
		
		return rtnItem;
	}
	
	@SuppressWarnings( "unchecked" )
	protected JSONObject getMapResponse( Map< String, Object > map ) {
		JSONObject rtnItem = new JSONObject();
		rtnItem.put( "resCode", GsntalkAPIResponse.SUCCESS.getResCode() );
		
		for( String key : map.keySet() ) {
			rtnItem.put( key, map.get( key ) );
		}
		
		return rtnItem;
	}
	
	@SuppressWarnings( "unchecked" )
	protected JSONObject getMapResponse( HttpServletResponse response, String jwtToken, Map< String, Object > map ) {
		JSONObject rtnItem = new JSONObject();
		rtnItem.put( "resCode", GsntalkAPIResponse.SUCCESS.getResCode() );
		
		for( String key : map.keySet() ) {
			rtnItem.put( key, map.get( key ) );
		}
		
		// set token to response
		response.addHeader( GsntalkConstants.GSN_TALK_TOKEN_KEY, jwtToken );
		
		return rtnItem;
	}
}