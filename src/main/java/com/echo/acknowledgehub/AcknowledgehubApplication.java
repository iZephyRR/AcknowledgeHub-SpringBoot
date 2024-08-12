package com.echo.acknowledgehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class AcknowledgehubApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcknowledgehubApplication.class, args);
	}

}
