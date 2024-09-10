package com.echo.acknowledgehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication(proxyBeanMethods = false)
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
@EnableCaching(proxyTargetClass = true)
public class AcknowledgehubApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcknowledgehubApplication.class, args);
	}

}
