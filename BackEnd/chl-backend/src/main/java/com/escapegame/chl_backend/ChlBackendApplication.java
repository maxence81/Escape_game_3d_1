package com.escapegame.chl_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChlBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChlBackendApplication.class, args);
		System.out.println("✅ Servidor Backend Escape Game iniciado en: http://localhost:8080");
	}

}
