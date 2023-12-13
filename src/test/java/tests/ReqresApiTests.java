package tests;

import io.restassured.http.ContentType;
import models.lombok.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.RequestSpec.requestSpec;
import static specs.RequestSpec.responseSpec;


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
    @DisplayName("Check defined user was registered")
    void definedRegisterUserTest() {
        RegistrationRequestModel registrationBody = new RegistrationRequestModel();
        registrationBody.setEmail("eve.holt@reqres.in");
        registrationBody.setPassword("pistol");

        RegistrationResponseModel response = step("Registration request", () -> {
            return given()
                    .spec(requestSpec)
                    .body(registrationBody)
                    .when()
                    .post("/register")
                    .then()
                    .spec(responseSpec)
                    .statusCode(200)
                    .extract().as(RegistrationResponseModel.class);
        });

        step("Verify response", () -> assertEquals("QpwL5tke4Pnpja7X4", response.getToken()));
    }

    @Test
    @DisplayName("Check non-defined user was registered")
    void nonDefinedRegisterUserTest() {
        RegistrationRequestModel registrationBody = new RegistrationRequestModel();
        registrationBody.setEmail("eve.123t@test");
        registrationBody.setPassword("pistol-non-1");

        RegistrationFailedResponseModel response = step("Registration request", () -> {
            return given()
                    .spec(requestSpec)
                    .body(registrationBody)
                    .when()
                    .post("/register")
                    .then()
                    .spec(responseSpec)
                    .statusCode(400)
                    .extract().as(RegistrationFailedResponseModel.class);
        });

        step("Verify response", () ->
                assertEquals("Note: Only defined users succeed registration", response.getError()));
    }

    @Test
    void singleUserFoundTest1() {

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
    @DisplayName("Check data for found user")
    void singleUserFoundTest() {
        UserResponseModel response = step("Single user request", () -> {
            return given()
                    .spec(requestSpec)
                    .when()
                    .get("/users/2")
                    .then()
                    .spec(responseSpec)
                    .statusCode(200)
                    .extract().as(UserResponseModel.class);
        });

        step("Verify response", () -> {
            assertEquals("janet.weaver@reqres.in", response.getData().getEmail());
            assertEquals("Janet", response.getData().getFirstName());
            assertEquals("Weaver", response.getData().getLastName());
        });
    }

    @Test
    void singleUserNotFoundTest1() {

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
    @DisplayName("Check status for non-existing user")
    void singleUserNotFoundTest() {
        step("Single user request", () -> {
            given()
                    .spec(requestSpec)
                    .when()
                    .get("/users/23")
                    .then()
                    .spec(responseSpec)
                    .statusCode(404);
        });
    }

    @Test
    @DisplayName("delete")
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
    void successfulCreateUserTest1() {
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
    @DisplayName("Verify data of created user")
    void successfulCreateUserTest() {
        final String name = "Adrian Doe Jr";
        final String job = "Java developer";

        CreateUserRequestModel regBody = new CreateUserRequestModel();
        regBody.setName(name);
        regBody.setJob(job);

        CreateUserResponseModel response = step("Single user request", () -> {
            return given()
                    .spec(requestSpec)
                    .body(regBody)
                    .when()
                    .post("/users")
                    .then().log().status()
                    .log().body()
                    .statusCode(201)
                    .extract().as(CreateUserResponseModel.class);
        });

        step("Verify response", () -> {
            assertEquals(name, response.getName());
            assertEquals(job, response.getJob());
        });

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