package com.gsntalk.api.common.extend;

import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gsntalk.api.util.GsntalkIFUtil;
import com.gsntalk.api.util.GsntalkS3Util;

public class CommonService {

	protected Logger LOGGER;
	
	@Autowired
	protected GsntalkIFUtil gsntalkIFUtil;
	
	@Autowired
	protected GsntalkS3Util gsntalkS3Util;
	
	protected JSONParser jsonParser;
	
	public CommonService( Class<?> c ) {
		this.LOGGER = LoggerFactory.getLogger( c );
		jsonParser = new JSONParser();
	}
}