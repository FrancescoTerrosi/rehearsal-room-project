package org.unifi.ft.rehearsal.features;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.unifi.ft.rehearsal.RehearsalRoomApplication;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/ete-test/resources/login.feature")
public class LoginPageEndToEndBDD {

	@BeforeClass
	public static void setupClass() {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(RehearsalRoomApplication.class);
		builder.run(new String[] { "--server.port=11111" });
	}

}
