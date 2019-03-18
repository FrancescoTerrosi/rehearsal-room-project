package org.unifi.ft.rehearsal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.unifi.ft.rehearsal.configurations.MongoConfig;
import org.unifi.ft.rehearsal.configurations.WebSecurityConfig;

@SpringBootApplication
@Import({WebSecurityConfig.class, MongoConfig.class})
public class RehearsalRoomApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RehearsalRoomApplication.class, args);
	}

}
