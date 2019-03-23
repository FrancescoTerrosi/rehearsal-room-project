package org.unifi.ft.rehearsal.features;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/e2e/resources/login.feature")
public class LoginPageEndToEndBDD {


}
