package com.echo.acknowledgehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
//@EnableAsync
public class AcknowledgehubApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcknowledgehubApplication.class, args);
	}

}
