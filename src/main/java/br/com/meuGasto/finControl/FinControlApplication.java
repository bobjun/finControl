package br.com.meuGasto.finControl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableScheduling
@EnableRetry
@EnableConfigurationProperties
public class FinControlApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(FinControlApplication.class);
		application.setAddCommandLineProperties(true);
		application.run(args);
	}

}
