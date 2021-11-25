package com.trailiva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication
public class TrailivaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrailivaApplication.class, args);
	}

}
