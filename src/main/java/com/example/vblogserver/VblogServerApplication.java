package com.example.vblogserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class VblogServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VblogServerApplication.class, args);
	}

}
