package DZ3;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;


@Story("Account api test")
public class AccountTest extends BaseTest {
    @Test
    public void  getAccountInfoTest(){
        given()
                .headers("Authorisation",token)
                .when()
                .get("https://api.imgur.com/3/account/{username}", username)
                .then()
                .statusCode(200);
    }

    @Test
    public void  getAccountInfoTestWithLoggingTest() {
        given()
                .headers("Authorisation", "Bearer d07c2245276fe0af3a41709640e9347fd380a9ee")
                .log()
                .method()
                .log()
                .uri()
                .when()
                .get("https://api.imgur.com/3/account/Test13")
                .prettyPeek()
                .then()
                .statusCode(200);

    }
}
