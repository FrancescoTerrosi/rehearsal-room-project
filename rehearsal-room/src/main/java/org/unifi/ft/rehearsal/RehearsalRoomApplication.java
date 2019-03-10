package org.unifi.ft.rehearsal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableMongoRepositories(basePackages = "org.unifi.ft.rehearsal.repository.mongo")
public class RehearsalRoomApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RehearsalRoomApplication.class, args);
	}

}
