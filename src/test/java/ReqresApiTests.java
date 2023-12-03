import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class ReqresApiTests {

    @Test
    void successfulRegisterUser() {
        final String authBody = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\"}";

        given()
                .log().uri()
                .log().method()
                .log().body()
                .body(authBody)
                .contentType(ContentType.JSON)
                .when()
                .post("https://reqres.in/api/register")
                .then().log().status()
                .log().body()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    void singleUserFound() {

        given()
                .log().uri()
                .log().method()
                .log().body()
                .when()
                .get("https://reqres.in/api/users/2")
                .then().log().status()
                .log().body()
                .statusCode(200)
                .body("data.email", is("janet.weaver@reqres.in"));
    }

    @Test
    void singleUserNotFound() {

        given()
                .log().uri()
                .log().method()
                .log().body()
                .when()
                .get("https://reqres.in/api/users/23")
                .then().log().status()
                .log().body()
                .statusCode(404);
    }

    @Test
    void singleResourceFound() {

        given()
                .log().uri()
                .log().method()
                .log().body()
                .when()
                .get("https://reqres.in/api/unlnown/2")
                .then().log().status()
                .log().body()
                .statusCode(200)
                .body("data.name", is("fuchsia rose"))
                .body("support.url", is("https://reqres.in/#support-heading"));
    }

    @Test
    void successfulCreateUser() {
        final String authBody = "{ \"name\": \"Adrian Doe\", \"job\": \"C# developer\"}";

        given()
                .log().uri()
                .log().method()
                .log().body()
                .body(authBody)
                .contentType(ContentType.JSON)
                .when()
                .post("https://reqres.in/api/users")
                .then().log().status()
                .log().body()
                .statusCode(201)
                .body("name", is("Adrian Doe"))
                .body("job", is("C# developer"));
    }


    @Test
    void delayedResponse() {

        given()
                .log().uri()
                .log().method()
                .log().body()
                .when()
                .get("https://reqres.in/api/users?delay=3")
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