package com.newsaggregator.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling; // MAKE SURE THIS IS HERE

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling // Enable Spring's scheduling capabilities
public class NewsAggregatorBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsAggregatorBackendApplication.class, args);
	}
}