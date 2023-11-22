package com.gsntalk.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GsntalkApiApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(GsntalkApiApplication.class, args);
	}
}