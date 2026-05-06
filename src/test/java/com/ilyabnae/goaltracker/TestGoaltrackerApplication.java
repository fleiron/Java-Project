package com.ilyabnae.goaltracker;

import org.springframework.boot.SpringApplication;

public class TestGoaltrackerApplication {

	public static void main(String[] args) {
		SpringApplication.from(GoaltrackerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
