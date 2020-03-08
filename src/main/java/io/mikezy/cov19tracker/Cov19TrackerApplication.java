package io.mikezy.cov19tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Cov19TrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Cov19TrackerApplication.class, args);
	}

}
