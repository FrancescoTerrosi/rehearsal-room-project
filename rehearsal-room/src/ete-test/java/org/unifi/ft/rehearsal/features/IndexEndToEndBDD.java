package org.unifi.ft.rehearsal.features;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.unifi.ft.rehearsal.RehearsalRoomApplication;
import org.unifi.ft.rehearsal.configurations.MongoConfig;
import org.unifi.ft.rehearsal.configurations.WebSecurityConfig;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/ete-test/resources/index.feature")
@Import({WebSecurityConfig.class, MongoConfig.class})
@SpringBootTest
public class IndexEndToEndBDD {

	@BeforeClass
	public static void setupClass() {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(RehearsalRoomApplication.class);
		builder.run(new String[] { "--server.port=11111" });
	}
	
}
