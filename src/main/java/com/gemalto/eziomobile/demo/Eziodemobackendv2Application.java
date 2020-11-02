package com.gemalto.eziomobile.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Eziodemobackendv2Application extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(Eziodemobackendv2Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(Eziodemobackendv2Application.class);
	}
}
