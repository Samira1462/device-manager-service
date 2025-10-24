package com.codechallenge.devicemanagerservice.api;

import com.codechallenge.devicemanagerservice.AbstractTest;
import com.codechallenge.devicemanagerservice.dto.DeviceDto;
import com.codechallenge.devicemanagerservice.service.DeviceService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;


import static org.hamcrest.Matchers.*;

class DeviceManagerApiTest extends AbstractTest {

    @Inject
    DeviceService testAssistant;

    @LocalServerPort
    int port;

    @Nested
    class SaveTests {

        @Test
        void givenDto_whenSaveOne_thenReturnIdWithCreatedStatus() {

            var givenBody = new DeviceDto();
            givenBody.setName("Device A");
            givenBody.setBrand("Brand X");
            givenBody.setState("AVAILABLE");

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .baseUri("http://localhost").port(port).basePath("/app/devices")
                    .body(givenBody)
                    .when().post()
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .header("Location", containsString("/app/devices"))
                    .body("", notNullValue())
                    .log().all(true);
        }

        @Test
        void givenInvalidDto_whenSaveOne_thenReturnBadRequest() {
            var invalidGivenBody = new DeviceDto();
            invalidGivenBody.setBrand("Brand X");
            invalidGivenBody.setState("AVAILABLE");

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .baseUri("http://localhost").port(port).basePath("/app/devices")
                    .body(invalidGivenBody)
                    .when().post()
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", containsString("invalid values"))
                    .body("details", hasItem(containsString("Name is required")))
                    .log().all(true);
        }

    }




}