package org.unifi.ft.rehearsal.features.steps;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.github.bonigarcia.wdm.ChromeDriverManager;

@SpringBootTest
@ContextConfiguration(loader = SpringBootContextLoader.class)
public class LoginPageCucumberSteps {

	private static final String HOMEPAGE = "http://localhost:";

	private int port = 11111;

	private WebDriver driver;

	@Autowired
	private IBandDetailsMongoRepository repository;

	@BeforeClass
	public static void setupClass() {
		ChromeDriverManager.getInstance().setup();
	}

	@Before
	public void setupDriver() {
		driver = new ChromeDriver();
		BandDetails b = new BandDetails("Username", "UserPassword", "USER");
		repository.save(b);
	}

	@After
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
		repository.deleteAll();
	}

}
