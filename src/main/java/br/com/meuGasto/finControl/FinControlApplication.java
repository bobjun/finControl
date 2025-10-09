package br.com.meuGasto.finControl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinControlApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinControlApplication.class, args);
	}

}
