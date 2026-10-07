package com.concord.circulationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CirculationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CirculationServiceApplication.class, args);
	}

}
