package com.renthub;

import org.springframework.boot.SpringApplication;

public class TestBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(RentHubApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
