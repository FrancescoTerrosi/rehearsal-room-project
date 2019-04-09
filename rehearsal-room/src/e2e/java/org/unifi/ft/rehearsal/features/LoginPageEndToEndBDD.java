package org.unifi.ft.rehearsal.features;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import io.github.bonigarcia.wdm.WebDriverManager;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/e2e/resources/login.feature")
public class LoginPageEndToEndBDD {

	@BeforeClass
	public static void setupClass() {
		WebDriverManager.chromedriver().setup();
	}

}
