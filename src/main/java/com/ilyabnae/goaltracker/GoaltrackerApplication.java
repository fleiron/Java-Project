package com.ilyabnae.goaltracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Точка входу Spring Boot застосунку. @SpringBootApplication
// вмикає auto-configuration та сканування компонентів у пакеті com.ilyabnae.goaltracker.*
@SpringBootApplication
public class GoaltrackerApplication {

	public static void main(String[] args) {
		// Запускає вбудований Tomcat і піднімає контекст Spring (контролери, сервіси, БД)
		SpringApplication.run(GoaltrackerApplication.class, args);
	}

}
