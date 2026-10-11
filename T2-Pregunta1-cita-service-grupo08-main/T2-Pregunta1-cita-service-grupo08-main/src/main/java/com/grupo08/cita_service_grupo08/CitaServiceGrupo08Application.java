package com.grupo08.cita_service_grupo08;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CitaServiceGrupo08Application {

	public static void main(String[] args) {
		SpringApplication.run(CitaServiceGrupo08Application.class, args);
	}

}
