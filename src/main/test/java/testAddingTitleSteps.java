
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;


import static org.fest.assertions.Assertions.assertThat;

public class testAddingTitleSteps {

    private Calculator c;
    private double first;
    private double second;
    private String sign;

    @Given("a system with $initial number of customers")
    public void givenACalculatorIJustTurnedOn(int num) {

    }

    @When("I add a new customer")
    public void when2(double val2) {
        this.second = val2;
    }

    @Then("system should have $result cusomers")
    public void thenTheResultIs(long val) {

    }
}
