package com.example.m5_room_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class M5RoomServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(M5RoomServiceApplication.class, args);
	}

}
