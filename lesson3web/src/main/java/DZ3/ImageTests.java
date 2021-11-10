package DZ3;

import io.qameta.allure.Story;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

@Story("Image tests")
public class ImageTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/imagefortest.jpg";
    static String encodedfile;
    static String uploadedImageId;

    @BeforeEach
    void beforeTest(){
        byte[] byteArray=getFileContent();
        encodedfile= Base64.getEncoder().encodeToString(byteArray);
    }
    @DisplayName("Загрузка файла в формате base64")
    @Test
    void uploadFileTest() {
        uploadedImageId = given()
                .headers("Authorisation", token)
                .multiPart("image", encodedfile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }
    @DisplayName("Загрузка без картинки")
    @Test
    void imageUploadWithoutImage(){
        given()
                .headers("Authorisation", token)
                .log()
                .uri()
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .statusCode(400);

    }
    @DisplayName("Добавление в избранное")
    @Test
    void favoriteAnImage(){
        given()
                .header("Authorisation", token)
                .expect()
                .body("success", is(true))
                .body("data.url", equalTo(username))
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image/{{imageHash}}/favorite")
                .prettyPeek()
                .then()
                .contentType("application/json");

    }
    @DisplayName("Загрузка файла")
    @Test
    void uploadFileImageTest(){
        uploadedImageId = given()
                .headers("Authorisation", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }
    @DisplayName("Get Image")
    @Test
    void getImage(){
        given()
                .header("Authorisation", token)
                .expect()
                .body("data.url", is("<username>"))
                .statusCode(200)
                .when()
                .get("https://api.imgur.com/3/image/{{imageHash}}")
                .prettyPeek()
                .then()
                .contentType("application/json");
    }

    @DisplayName("Изменение информации файла")
    @Test
    void updateImageInformation() {
        given()
                .header("Authorisation", token)
                .formParam("title", "Imagenew")
                .expect()
                .body("title", is("Imagenew"))
                .when()
                .post("https://api.imgur.com/3/image/{{imageHash}}")
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @AfterEach
    void tearDown(){
        given()
                .headers("Authorisation", token)
                .when()
                .delete("https://api.imgur.com/3/image/{{imageDeleteHash}}","testprogmath",uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    private byte[] getFileContent(){
        byte[] byteArray = new byte[0];
        try{
            byteArray= FileUtils.readFileToByteArray(new File( PATH_TO_IMAGE));
        }catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

}


