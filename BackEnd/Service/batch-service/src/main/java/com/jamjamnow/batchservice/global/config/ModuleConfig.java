package com.jamjamnow.batchservice.global.config;

import com.jamjamnow.batchservice.global.infrastructor.openapi.OpenApiRegistryProperties;
import com.jamjamnow.commonmodule.config.CommonModuleConfig;
import com.jamjamnow.persistencemodule.global.config.PersistenceModuleConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    CommonModuleConfig.class,
    PersistenceModuleConfig.class
})
@EnableConfigurationProperties(OpenApiRegistryProperties.class)
public class ModuleConfig {

}
