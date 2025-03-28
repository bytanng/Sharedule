package com.sharedule.app;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShareduleApplicationTests {

	static {
		Dotenv dotenv = Dotenv.configure()
				.load();

		// Set the environment variables in the system properties
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);
	}

	@Test
	void contextLoads() {
		// Test context loading here
	}
}
