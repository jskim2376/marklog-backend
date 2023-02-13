package com.marklog.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaAuditing
@EnableTransactionManagement(order = 0)
@EnableGlobalMethodSecurity(prePostEnabled = true, order = 1)
@SpringBootApplication
public class MarklogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarklogApplication.class, args);
		return;
	}

}
