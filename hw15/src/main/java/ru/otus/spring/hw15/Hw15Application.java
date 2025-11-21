package ru.otus.spring.hw15;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties
public class Hw15Application {

	public static void main(String[] args) {
		SpringApplication.run(Hw15Application.class, args);
	}
}
