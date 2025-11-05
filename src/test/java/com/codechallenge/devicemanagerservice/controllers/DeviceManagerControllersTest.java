package com.codechallenge.devicemanagerservice.controllers;

import com.codechallenge.devicemanagerservice.AbstractTest;
import com.codechallenge.devicemanagerservice.dto.DeviceCreateDto;
import com.codechallenge.devicemanagerservice.dto.DeviceResponseDto;
import com.codechallenge.devicemanagerservice.dto.DeviceUpdateDto;
import com.codechallenge.devicemanagerservice.model.DeviceEntity;
import com.codechallenge.devicemanagerservice.model.DeviceState;
import com.codechallenge.devicemanagerservice.repository.DeviceRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

class DeviceManagerControllersTest extends AbstractTest {

    @Inject
    DeviceRepository deviceRepository;

    @Nested
    class SaveTests {

        @Test
        void givenDto_whenSaveOne_thenReturnIdWithCreatedStatus() {

            var givenBody = new DeviceCreateDto();
            givenBody.setName("Device A");
            givenBody.setBrand("Brand X");
            givenBody.setState(DeviceState.valueOf("AVAILABLE"));

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .baseUri("http://localhost").port(port).basePath("/api/devices")
                    .body(givenBody)
                    .when().post()
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .header("Location", containsString("/api/devices"))
                    .body("", notNullValue())
                    .log().all(true);
        }

        @Test
        void givenInvalidDto_whenSaveOne_thenReturnBadRequest() {
            var invalidDto = new DeviceCreateDto();
            invalidDto.setBrand("Brand X");
            invalidDto.setState(DeviceState.valueOf("AVAILABLE"));

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .baseUri("http://localhost").port(port).basePath("/api/devices")
                    .body(invalidDto)
                    .when().post()
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("details.field", hasItem("name"))
                    .body("details.message", hasItem("Name is required"))
                    .log().all(true);
        }

    }

    @Nested
    class UpdateTests {

        private DeviceEntity existingDevice;

        @BeforeEach
        void setUpDevice() {
            deviceRepository.deleteAll();
            existingDevice = new DeviceEntity();
            existingDevice.setName("Device A");
            existingDevice.setBrand("Brand X");
            existingDevice.setState(DeviceState.AVAILABLE);
            existingDevice = deviceRepository.save(existingDevice);
        }

        @Test
        void givenValidUpdateDto_whenUpdate_thenReturnUpdatedDevice() {
            var updateDto = new DeviceUpdateDto();
            updateDto.setName("Device A Updated");
            updateDto.setBrand("Brand X Updated");
            updateDto.setState(DeviceState.valueOf("IN_USE"));

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .baseUri("http://localhost")
                    .port(port)
                    .basePath("/api/devices/" + existingDevice.getId())
                    .body(updateDto)
                    .when().put()
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(existingDevice.getId().intValue()))
                    .body("name", equalTo("Device A Updated"))
                    .body("brand", equalTo("Brand X Updated"))
                    .body("state", equalTo("IN_USE"))
                    .log().all(true);
        }

        @Test
        void givenInUseDevice_whenUpdateNameOrBrand_thenReturnBadRequest() {
            existingDevice.setState(DeviceState.IN_USE);
            deviceRepository.save(existingDevice);

            var updateDto = new DeviceUpdateDto();
            updateDto.setName("Device A Updated");
            updateDto.setBrand("Brand X Updated");
            updateDto.setState(DeviceState.valueOf("IN_USE"));

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .baseUri("http://localhost")
                    .port(port)
                    .basePath("/api/devices/" + existingDevice.getId())
                    .body(updateDto)
                    .when().put()
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", containsString("Cannot update name or brand when device is in use"))
                    .log().all(true);
        }

        @Test
        void givenNonExistingDevice_whenUpdate_thenReturnNotFound() {
            var updateDto = new DeviceUpdateDto();
            updateDto.setName("NonExistent");
            updateDto.setBrand("NonExistent");
            updateDto.setState(DeviceState.valueOf("AVAILABLE"));

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .baseUri("http://localhost")
                    .port(port)
                    .basePath("/api/devices/1000")
                    .body(updateDto)
                    .when().put()
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString("Device not found"))
                    .body("$", not(hasKey("errors")))
                    .log().all(true);
        }

        @Test
        void givenInvalidDto_whenUpdate_thenReturnValidationErrors() {

            DeviceUpdateDto invalidDto = new DeviceUpdateDto();
            invalidDto.setName("");

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .port(port)
                    .basePath("/api/devices/{id}")
                    .pathParam("id", existingDevice.getId())
                    .body(invalidDto)
                    .when()
                    .put()
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Invalid input values"))
                    .log().all(true);
        }
    }

    @Nested
    class GetByIdTests {

        @Test
        void givenExistingDevice_whenGetById_thenReturnDevice() {

            var createDto = new DeviceCreateDto();
            createDto.setName("Device A");
            createDto.setBrand("Brand X");
            createDto.setState(DeviceState.valueOf("AVAILABLE"));

            var createdResponse = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .baseUri("http://localhost")
                    .port(port)
                    .basePath("/api/devices")
                    .body(createDto)
                    .when().post()
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract().as(DeviceResponseDto.class);

            Long deviceId = createdResponse.getId();

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .baseUri("http://localhost")
                    .port(port)
                    .basePath("/api/devices/" + deviceId)
                    .when().get()
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(deviceId.intValue()))
                    .body("name", equalTo("Device A"))
                    .body("brand", equalTo("Brand X"))
                    .body("state", equalTo("AVAILABLE"))
                    .log().all(true);
        }

        @Test
        void givenNonExistingDevice_whenGetById_thenReturnNotFound() {
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .baseUri("http://localhost")
                    .port(port)
                    .basePath("/api/devices/1000")
                    .when().get()
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString("Device not found with id: 1000"))
                    .log().all(true);
        }
    }

    @Nested
    class GetAllTests {

        @Test
        void givenDevicesExist_whenGetAll_thenReturnPagedDevices() {
            // create a device
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .port(port)
                    .basePath("/api/devices")
                    .body(new DeviceCreateDto("Device D", "Brand W", DeviceState.valueOf("AVAILABLE")))
                    .when().post()
                    .then().statusCode(HttpStatus.CREATED.value());

            RestAssured.given()
                    .port(port)
                    .basePath("/api/devices")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when().get()
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content.size()", greaterThan(0))
                    .body("content.name", hasItem("Device D"));
        }
    }

    @Nested
    class DeleteTests {
        private DeviceEntity existingDevice;

        @BeforeEach
        void init() {
            deviceRepository.deleteAll();

            DeviceEntity entity = new DeviceEntity();
            entity.setName("RemovableDevice");
            entity.setBrand("BrandA");
            entity.setState(DeviceState.AVAILABLE);
            entity.setCreatedAt(Instant.now());
            entity.setUpdatedAt(Instant.now());

            existingDevice = deviceRepository.save(entity);
        }

        @Test
        void givenExistingDevice_whenDelete_thenReturnNoContentAndRemoveFromDB() {

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/api/devices/{id}", existingDevice.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value())
                    .log().all();

            assertThat(deviceRepository.findById(existingDevice.getId())).isEmpty();
        }

        @Test
        void givenNonExistingDevice_whenDelete_thenReturnNotFound() {
            long invalidId = 1000L;

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/api/devices/{id}", invalidId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString("Device not found with id: " + invalidId))
                    .body("timestamp", notNullValue())
                    .log().all();
        }

        @Test
        void givenDeviceInUse_whenDelete_thenReturnConflict() {

            existingDevice.setState(DeviceState.IN_USE);
            Long inUsedDeviceId = deviceRepository.save(existingDevice).getId();

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when()
                    .delete("/api/devices/{id}", inUsedDeviceId)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body("message", containsString("Cannot delete device in use"))
                    .body("timestamp", notNullValue())
                    .log().all();

            assertThat(deviceRepository.findById(inUsedDeviceId)).isPresent();
        }
    }

    @Nested
    class SearchTests {
        @Test
        void givenBrandAndState_whenSearchDevices_thenReturnPagedResponse() {
            var device1 = new DeviceEntity();
            device1.setName("MacBook Pro");
            device1.setBrand("Apple");
            device1.setState(DeviceState.AVAILABLE);
            device1.setCreatedAt(Instant.now());
            device1.setUpdatedAt(Instant.now());

            var device2 = new DeviceEntity();
            device2.setName("MacBook Air");
            device2.setBrand("Apple");
            device2.setState(DeviceState.AVAILABLE);
            device2.setCreatedAt(Instant.now());
            device2.setUpdatedAt(Instant.now());

            deviceRepository.saveAll(List.of(device1, device2));

            RestAssured.given()
                    .port(port)
                    .contentType(ContentType.JSON)
                    .queryParam("brand", "Apple")
                    .queryParam("state", "AVAILABLE")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/api/devices/search")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content.size()", equalTo(2))
                    .body("content[0].name", equalTo(device1.getName()))
                    .body("content[1].name", equalTo(device2.getName()))
                    .body("totalElements", equalTo(2))
                    .body("totalPages", equalTo(1))
                    .log().all();
        }

        @Test
        void givenNoFilter_whenSearchDevices_thenReturnAllDevices() {
            var device1 = new DeviceEntity();
            device1.setName("MacBook Pro");
            device1.setBrand("Apple");
            device1.setState(DeviceState.AVAILABLE);
            device1.setCreatedAt(Instant.now());
            device1.setUpdatedAt(Instant.now());

            var device2 = new DeviceEntity();
            device2.setName("MacBook Air");
            device2.setBrand("Apple");
            device2.setState(DeviceState.AVAILABLE);
            device2.setCreatedAt(Instant.now());
            device2.setUpdatedAt(Instant.now());

            deviceRepository.saveAll(List.of(device1, device2));

            RestAssured.given()
                    .port(port)
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/api/devices/search")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content.size()", greaterThanOrEqualTo(2))
                    .log().all();
        }
    }

}