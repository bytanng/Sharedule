package com.sharedule.app;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
  MongoAutoConfiguration.class, 
  MongoDataAutoConfiguration.class
})
public class ShareduleApplication {

	public static void main(String[] args) {


		SpringApplication.run(ShareduleApplication.class, args);
	}

}
