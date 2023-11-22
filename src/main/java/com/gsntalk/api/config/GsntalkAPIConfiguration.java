package com.gsntalk.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GsntalkAPIConfiguration implements WebMvcConfigurer {

	private Logger logger = LoggerFactory.getLogger( this.getClass() );
	
	@Override
	public void addCorsMappings( CorsRegistry corsRegistry ) {
		corsRegistry.addMapping( "*/**" )
					.allowedOrigins(
										"http://localhost:3000",
										"https://localhost:3000",
										"https://dev.xn--ob0bz92b4xbc9t.com",
										"https://dev.공실앤톡.com"
					).exposedHeaders( "gsntalk-token" )
					.allowCredentials( true );
		
		logger.info( "@@@@@@@@@@@@@@@@@@ WebMvcConfigurer - addCorsMapping -> done." );
	}
}