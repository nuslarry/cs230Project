package com.example.demo;

import com.example.demo.service.HDFSAccess;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
public class Cs230Project1Application {

	public static void main(String[] args) throws IOException, URISyntaxException {
		HDFSAccess.getInstance();
		SpringApplication.run(Cs230Project1Application.class, args);
	}

}
