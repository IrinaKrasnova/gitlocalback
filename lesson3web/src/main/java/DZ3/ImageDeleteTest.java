package DZ3;

import DZ3.dto.PostImageResponse;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static DZ3.ImageUploadTests.requestSpecificationWithAuthWithBase64;
import static io.restassured.RestAssured.given;

public class ImageDeleteTest extends BaseTest{
    private final String PATH_TO_IMAGE = "src/test/resources/imagefortest.jpg";
    static String encodedfile;
    static String uploadedImageId;
    @BeforeEach
    void setUp
    private final MultiPartSpecification base64MultiPartSpec;

    {
        byte[] byteArray=getFileContent(PATH_TO_IMAGE);
        encodedfile= Base64.getEncoder().encodeToString(byteArray);

        base64MultiPartSpec=new MultiPartSpecBuilder(encodedfile)
                .controlName("image")
                .build();

        requestSpecificationWithAuth=new RequestSpecBuilder()
                .addHeader("Authorisation", token)
                .addMultiPart(base64MultiPartSpec)
                .build();

        uploadedImageId = given(requestSpecificationWithAuthWithBase64,positiveResponseSpecification)

                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getDeleteHash;
    }
    @Test
    void deleteImage {
        given(requestSpecificationWithAuth)
                .when()
                .delete("https://api.imgur.com/3/image/{{imageDeleteHash}}","Test13",uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);

    }
}
