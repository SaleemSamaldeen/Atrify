package stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RestServices {

    private static String customerID;

    private static String baseURL;

    private static String order;

    private static String quantity;

    private RequestSpecification requestSpecification;

    private Response response;

    private JSONObject requestBody;


    @Given("the base url {string} to place Donut order")
    public void baseURL(String endpoint) {
        baseURL = endpoint;
        RestAssured.baseURI = baseURL;
    }

    @When("customer register Donut site with {string} and {string}")
    public void registerDonutSite(String name, String mailID) {
        requestBody = new JSONObject();
        requestBody.put("first_name", name.split(" ")[0]);
        requestBody.put("last_name", name.split(" ")[1]);
        requestBody.put("email", mailID);
        response = requestSpecification.body(requestBody.toJSONString())
                .header("Content-Type", "application/json")
                .request(Method.POST);
        assertResponseCode("POST", baseURL, response);
    }

    @Then("Generated customer ID sent to his {string}")
    public void generateCustomerIDToCustomer() {
        customerID = response.getBody().jsonPath().get("customerID");
    }

    @When("customer with {string} choose Donut {string} and {string}")
    public void customerChooseOrderAndQuantity(String customerID, String product, String quantity) {
        requestBody = new JSONObject();
        requestBody.put("id", customerID);
        requestBody.put("product", product);
        requestBody.put("quantity", quantity);
        response = requestSpecification.body(requestBody.toJSONString())
                .header("Content-Type", "application/json")
                .request(Method.POST);
        assertResponseCode("POST", baseURL, response);
        assertResponseBody("POST", baseURL, response, "order is placed");
    }

    @Then("Jim collects the number of orders")
    public void collectPlacedOrders() {
        response = requestSpecification.request(Method.GET);
        assertResponseCode("GET", baseURL, response);
        List<HashMap> responseList = response.jsonPath().get("data");
    }

    @Then("customer wants to cancel existing order")
    public void customerCancelOrder() {
        requestBody = new JSONObject();
        requestBody.put("id", customerID);
        requestBody.put("order_no", order);
        requestBody.put("quantity", quantity);
        response = requestSpecification.body(requestBody.toJSONString())
                .header("Content-Type", "application/json")
                .request(Method.POST);
        assertResponseCode("POST", baseURL, response);
        assertResponseBody("POST", baseURL, response, "order is cancelled");
    }

    @And("check if the customer ID removed from order list")
    public void checkCustomerIDRemovedFromList() {
        response = requestSpecification.request(Method.GET);
        assertResponseCode("GET", baseURL, response);
        List<HashMap> responseList = response.jsonPath().get("data");
        for (Map<String,String> orders : responseList) {
            assertThat(orders.get("id"))
                    .isNotEqualTo(customerID)
                    .withFailMessage("Customer ID not removed from orders");
        }
    }

    @When("customer with {string} wants to delete his account")
    public void customerDeleteAccount(String customerID) {
        requestBody = new JSONObject();
        requestBody.put("id", customerID);
        response = requestSpecification.body(requestBody.toJSONString())
                .header("Content-Type", "application/json")
                .request(Method.DELETE);
        assertResponseCode("DELETE", baseURL, response);
        assertResponseBody("DELETE", baseURL, response, "Customer ID is deleted");
    }

    @When("jim tries to fetch invalid {string} order")
    public void fetchInvalidCustomerOrder(String invalidCustomer) {
        requestBody = new JSONObject();
        requestBody.put("id", invalidCustomer);
        response = requestSpecification.body(requestBody.toJSONString())
                .header("Content-Type", "application/json")
                .request(Method.POST);
        assertResponseCode("POST", baseURL, response);
        assertResponseBody("POST", baseURL, response, "Invalid Customer number");
    }

    @Then("remove customer details from database")
    public void removeCustomerFromDB() {

    }

    public void assertResponseCode(String method, String baseURI, Response response) {
        assertThat(response.getStatusCode())
                .isEqualTo(200)
                .withFailMessage(method + baseURI + "response is not 200");
    }

    public void assertResponseBody(String method, String baseURI, Response response, String expectedBody) {
        assertThat(response.getBody()).isEqualTo(expectedBody)
                .withFailMessage(method + baseURI + "response not matches with " + expectedBody);
    }

}
