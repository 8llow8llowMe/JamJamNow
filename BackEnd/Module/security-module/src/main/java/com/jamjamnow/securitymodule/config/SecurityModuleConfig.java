package com.jamjamnow.securitymodule.config;

import com.jamjamnow.securitymodule.auth.jwt.JwtAuthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtAuthProperties.class)
@ComponentScan(basePackages = "com.jamjamnow.securitymodule")
public class SecurityModuleConfig {

}
