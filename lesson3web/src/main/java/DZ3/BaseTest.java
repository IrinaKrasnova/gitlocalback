package DZ3;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public abstract class BaseTest {
    static ResponseSpecification positiveResponseSpecification;
    static RequestSpecification requestSpecificationWithAuth;
    static Properties properties = new Properties();
    static String token;
    static String username;

    @BeforeAll
    static void beforeAll (){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters (new AllureRestAssured());
        getProperties();
        token= properties.getProperty("token");
        username= properties.getProperty("username");

        positiveResponseSpecification=  new ResponseSpecBuilder()
                .expectBody("status", equalTo(200))
                .expectBody("success", is(true))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();


        requestSpecificationWithAuth=  new RequestSpecBuilder()
                .addHeader("Authorisation", token)
                .build();
    }
    private static void getProperties(){
        try (InputStream output = new FileInputStream("src/main/resources/ingurproperties.properties")) {
            properties.load(output);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] getFileContent(String path){
        byte[] byteArray = new byte[0];
        try{
            byteArray= FileUtils.readFileToByteArray(new File( path));
        }catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

}