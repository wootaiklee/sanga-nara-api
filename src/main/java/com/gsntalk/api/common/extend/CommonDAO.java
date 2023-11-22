package com.gsntalk.api.common.extend;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CommonDAO {

	protected Logger LOGGER;
	
	@Autowired
	protected SqlSession sqlSession;
	
	public CommonDAO( Class<?> c ) {
		this.LOGGER = LoggerFactory.getLogger( c );
	}
}