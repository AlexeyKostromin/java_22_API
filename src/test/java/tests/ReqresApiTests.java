package tests;

import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.RequestSpec.requestSpec;
import static specs.RequestSpec.responseSpec;


public class ReqresApiTests extends BaseTest {

    @Test
    @DisplayName("Check defined user was registered")
    void definedRegisterUserTest() {
        UserRegistrationRequestModel registrationBody = new UserRegistrationRequestModel();
        registrationBody.setEmail("eve.holt@reqres.in");
        registrationBody.setPassword("pistol");

        UserRegistrationResponseModel response = step("Registration request", () -> {
            return given()
                    .spec(requestSpec)
                    .body(registrationBody)
                    .when()
                    .post("/register")
                    .then()
                    .spec(responseSpec)
                    .statusCode(200)
                    .extract().as(UserRegistrationResponseModel.class);
        });

        step("Verify response", () ->
                assertEquals("QpwL5tke4Pnpja7X4", response.getToken()));
    }

    @Test
    @DisplayName("Check non-defined user was NOT registered")
    void nonDefinedRegisterUserTest() {
        UserRegistrationRequestModel registrationBody = new UserRegistrationRequestModel();
        registrationBody.setEmail("eve.123t@test");
        registrationBody.setPassword("pistol-non-1");

        UserRegistrationFailedResponseModel response = step("Registration request", () -> {
            return given()
                    .spec(requestSpec)
                    .body(registrationBody)
                    .when()
                    .post("/register")
                    .then()
                    .spec(responseSpec)
                    .statusCode(400)
                    .extract().as(UserRegistrationFailedResponseModel.class);
        });

        step("Verify response", () ->
                assertEquals("Note: Only defined users succeed registration", response.getError()));
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
    @DisplayName("Verify data of created user")
    void successfulCreateUserTest() {
        final String name = "Adrian Doe Jr";
        final String job = "Java developer";

        UserCreateRequestModel regBody = new UserCreateRequestModel();
        regBody.setName(name);
        regBody.setJob(job);

        UserCreateResponseModel response = step("Single user request", () -> {
            return given()
                    .spec(requestSpec)
                    .body(regBody)
                    .when()
                    .post("/users")
                    .then()
                    .spec(responseSpec)
                    .statusCode(201)
                    .extract().as(UserCreateResponseModel.class);
        });

        step("Verify response", () -> {
            assertEquals(name, response.getName());
            assertEquals(job, response.getJob());
        });
    }

    @Test
    @DisplayName("Verify delayed request of GET LIST USERS")
    void delayedResponseTest() {

        UsersListResponseModel response = step("Delayed response of GET LIST USERS request", () -> {
            return given()
                    .spec(requestSpec)
                    .when()
                    .get("/users?delay=3")
                    .then().spec(responseSpec)
                    .statusCode(200)
                    .extract().as(UsersListResponseModel.class);
        });

        step("Verify response", () -> {
            assertEquals(6, response.getPerPage());
            assertEquals(12, response.getTotal());
            assertEquals(2, response.getTotalPages());
            UsersListResponseModel.UserData[] data = response.getData();
            var dataItem1 = data[1];
            assertEquals(2, dataItem1.getId());
            assertEquals("janet.weaver@reqres.in", dataItem1.getEmail());
            assertEquals("Janet", dataItem1.getFirstName());
            assertEquals("Weaver", dataItem1.getLastName());
        });
    }
}