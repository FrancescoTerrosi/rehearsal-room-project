package org.unifi.ft.rehearsal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;
import org.unifi.ft.rehearsal.repository.mongo.IScheduleMongoRepository;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = 	{org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository.class,
												org.unifi.ft.rehearsal.repository.mongo.IScheduleMongoRepository.class})
public class RehearsalRoomApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RehearsalRoomApplication.class, args);
	}

}
