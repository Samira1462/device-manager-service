package com.codechallenge.devicemanagerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DeviceManagerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeviceManagerServiceApplication.class, args);
	}

}
