package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import com.fx.market.kafka.message.FxRateEventProto;

public class ExampleSteps {

    @Given("I have an example")
    public void i_have_an_example() {
        System.out.println("Given step");

        FxRateEventProto proto = null;
    }

    @When("I run the example")
    public void i_run_the_example() {
        System.out.println("When step");
    }

    @Then("I should see the example result")
    public void i_should_see_the_example_result() {
        System.out.println("Then step");
    }
}
