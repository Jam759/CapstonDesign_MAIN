package com.Hoseo.CapstoneDesign.global.config;

import com.Hoseo.CapstoneDesign.global.logging.properties.LoggingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingConfig {
}