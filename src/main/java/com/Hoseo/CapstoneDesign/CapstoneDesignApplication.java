package com.Hoseo.CapstoneDesign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CapstoneDesignApplication {

    public static void main(String[] args) {
        SpringApplication.run(CapstoneDesignApplication.class, args);
    }

}
