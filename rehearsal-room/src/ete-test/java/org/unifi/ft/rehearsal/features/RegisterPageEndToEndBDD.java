package org.unifi.ft.rehearsal.features;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import io.github.bonigarcia.wdm.ChromeDriverManager;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/ete-test/resources/register.feature")
public class RegisterPageEndToEndBDD {


}
