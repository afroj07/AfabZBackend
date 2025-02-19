package com.afabz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class AfabzApplication {

	public static void main(String[] args) {
		SpringApplication.run(AfabzApplication.class, args);
	}

}
