package org.unifi.ft.rehearsal.features;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/ete-test/resources/index.feature")
public class IndexEndToEndBDD {

}
