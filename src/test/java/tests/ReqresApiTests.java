package tests;

import io.restassured.http.ContentType;
import models.lombok.RegistrationBodyRequest;
import models.lombok.RegistrationResponse;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.RegistrationSpecification.registerRequestSpec;
import static specs.RegistrationSpecification.registerResponseSpec;


public class ReqresApiTests extends BaseTest {

    @Test
    void successfulRegisterUserTest1() {
        final String authBody = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\"}";

        given()
                .log().uri()
                .log().method()
                .log().body()
                .body(authBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/register")
                .then().log().status()
                .log().body()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    void successfulRegisterUserTest() {
        RegistrationBodyRequest registrationBody = new RegistrationBodyRequest();
        registrationBody.setEmail("eve.holt@reqres.in");
        registrationBody.setPassword("pistol");

        RegistrationResponse response = step("Registration request", () -> {
            return given()
                    .spec(registerRequestSpec)
                    .body(registrationBody)
                    .when()
                    .post("/register")
                    .then()
                    .spec(registerResponseSpec)
                    .extract().as(RegistrationResponse.class);
        });

        step("Verify response", () -> assertEquals("QpwL5tke4Pnpja7X4", response.getToken()));

    }

    @Test
    void singleUserFoundTest() {

        given()
                .log().uri()
                .log().method()
                .log().body()
                .when()
                .get("/users/2")
                .then().log().status()
                .log().body()
                .statusCode(200)
                .body("data.email", is("janet.weaver@reqres.in"));
    }

    @Test
    void singleUserNotFoundTest() {

        given()
                .log().uri()
                .log().method()
                .log().body()
                .when()
                .get("/users/23")
                .then().log().status()
                .log().body()
                .statusCode(404);
    }

    @Test
    void singleResourceFoundTest() {

        given()
                .log().uri()
                .log().method()
                .log().body()
                .when()
                .get("unlnown/2")
                .then().log().status()
                .log().body()
                .statusCode(200)
                .body("data.name", is("fuchsia rose"))
                .body("support.url", is("https://reqres.in/#support-heading"));
    }

    @Test
    void successfulCreateUserTest() {
        final String authBody = "{ \"name\": \"Adrian Doe\", \"job\": \"C# developer\"}";

        given()
                .log().uri()
                .log().method()
                .log().body()
                .body(authBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/users")
                .then().log().status()
                .log().body()
                .statusCode(201)
                .body("name", is("Adrian Doe"))
                .body("job", is("C# developer"));
    }


    @Test
    void delayedResponseTest() {

        given()
                .log().uri()
                .log().method()
                .log().body()
                .when()
                .get("/users?delay=3")
                .then().log().status()
                .log().body()
                .statusCode(200)
                .body("page", is(1))
                .body("data",
                        hasItem(
                                allOf(
                                        hasEntry("first_name", "Emma"),
                                        hasEntry("email", "emma.wong@reqres.in")
                                )));
    }

}