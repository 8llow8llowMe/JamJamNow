package com.jamjamnow.apiservice.global.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {
    "com.jamjamnow.apiservice.domain",
    "com.jamjamnow.persistencemodule.domain"
})
@EnableJpaRepositories(basePackages = {
    "com.jamjamnow.apiservice.domain",
    "com.jamjamnow.persistencemodule.domain"
})
public class JpaConfig {

}
