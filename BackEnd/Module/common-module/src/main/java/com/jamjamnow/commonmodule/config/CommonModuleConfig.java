package com.jamjamnow.commonmodule.config;

import com.jamjamnow.commonmodule.property.JasyptProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JasyptProperties.class)
@ComponentScan(basePackages = "com.jamjamnow.commonmodule")
public class CommonModuleConfig {

}
