package com.gsntalk.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.CrossOrigin;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@CrossOrigin
public @interface GsntalkCORS {

	public String[] crossOrigins() default {
		"http://localhost:3000",
		"https://localhost:3000",
		"https://dev.xn--ob0bz92b4xbc9t.com",
		"https://dev.공실앤톡.com"
	};
}