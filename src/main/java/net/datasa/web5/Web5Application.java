package net.datasa.web5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // 무조건 프로그램 시작할때 시작하는것에 붙여주기 1단계!

@SpringBootApplication
public class Web5Application {

	public static void main(String[] args) {
		SpringApplication.run(Web5Application.class, args);
	}

}
