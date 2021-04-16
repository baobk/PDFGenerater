package com.gpch.pdfgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude={org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class} )
public class PdfgeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfgeneratorApplication.class, args);
    }

}
