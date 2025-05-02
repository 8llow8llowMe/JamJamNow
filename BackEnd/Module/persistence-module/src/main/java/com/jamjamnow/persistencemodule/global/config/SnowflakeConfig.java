package com.jamjamnow.persistencemodule.global.config;

import com.jamjamnow.persistencemodule.global.util.SnowflakeIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeConfig {

    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        // 서버마다 workerId / dataCenterId를 다르게 설정해줘야함
        return new SnowflakeIdGenerator(1, 1);
    }
}
