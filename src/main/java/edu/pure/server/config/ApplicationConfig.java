package edu.pure.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config.properties")
@PropertySource("classpath:secret.properties")
public class ApplicationConfig {
}
