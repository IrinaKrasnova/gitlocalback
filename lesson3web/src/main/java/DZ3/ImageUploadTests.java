package DZ3;

import DZ3.dto.PostImageResponse;
import io.qameta.allure.Story;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
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
public class ImageUploadTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/imagefortest.jpg";
    static String encodedfile;
    static String uploadedImageId;
    MultiPartSpecification base64MultiPartSpec;
    MultiPartSpecification multiPartSpecWithFile;
    static RequestSpecification requestSpecificationWithAuthAndMultiPartImage;
    static RequestSpecification requestSpecificationWithAuthWithBase64;


    @BeforeEach
    void beforeTest(){
        byte[] byteArray=getFileContent(PATH_TO_IMAGE);
        encodedfile= Base64.getEncoder().encodeToString(byteArray);

        base64MultiPartSpec=new MultiPartSpecBuilder(encodedfile)
                .controlName("image")
                .build();

        multiPartSpecWithFile=new MultiPartSpecBuilder(new File ("src/main/resources/ingurproperties.imagefortest.jpg")
               .controlName("image")
                .build();

        requestSpecificationWithAuthAndMultiPartImage=new RequestSpecBuilder()
                .addHeader("Authorisation", token)
                .addFormParam("title","imagenew")
                .addFormParam("type","gif")
                .addMultiPart(multiPartSpecWithFile)
                .build();

        requestSpecificationWithAuth=new RequestSpecBuilder()
                .addHeader("Authorisation", token)
                .addMultiPart(base64MultiPartSpec)
                .build();

    }
    @DisplayName("???????????????? ?????????? ?? ?????????????? base64")
    @Test
    void uploadFileTest() {
        uploadedImageId = given(requestSpecificationWithAuthWithBase64,positiveResponseSpecification)
               // .headers("Authorisation", token)
                //.multiPart("image", encodedfile)
                //.expect()
                //.body("success", is(true))
                //.body("data.id", is(notNullValue()))
                //.when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }
    @DisplayName("???????????????? ?????? ????????????????")
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
    @DisplayName("???????????????????? ?? ??????????????????")
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
    @DisplayName("???????????????? ??????????")
    @Test
    void uploadFileImageTest(){
        uploadedImageId = given(requestSpecificationWithAuthAndMultiPartImage)
                //.headers("Authorisation", token)
                //.multiPart("image", new File(PATH_TO_IMAGE))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(PostImageResponse.class)
                .getData().getDeletehash;
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

    @DisplayName("?????????????????? ???????????????????? ??????????")
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
                .delete("https://api.imgur.com/3/image/{{imageDeleteHash}}","Test13",uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }



}


