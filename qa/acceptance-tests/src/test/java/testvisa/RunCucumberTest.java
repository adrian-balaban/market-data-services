<<<<<<<< HEAD:qa/acceptance-tests/src/test/java/RunCucumberTest.java
package test.java;
========
package testvisa;
>>>>>>>> master:qa/acceptance-tests/src/test/java/testvisa/RunCucumberTest.java

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import io.cucumber.junit.platform.engine.Constants;

@Suite
@SelectClasspathResource("features/sample-domain/")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "stepdefinitions.fxmarket,stepdefinitions,helpers,testvisa")
@ConfigurationParameter(
        key = Constants.PLUGIN_PROPERTY_NAME,
        value = "pretty, json:build/reports/cucumber-report.json, html:build/reports/cucumber-report.html"
)
public class RunCucumberTest {

}