package com.example.m11_psychology_service;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
public class M11PsychologyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(M11PsychologyServiceApplication.class, args);
	}

}
