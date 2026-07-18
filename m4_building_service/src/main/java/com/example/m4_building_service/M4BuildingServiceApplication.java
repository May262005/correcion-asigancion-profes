package com.example.m4_building_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class M4BuildingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(M4BuildingServiceApplication.class, args);
	}

}
