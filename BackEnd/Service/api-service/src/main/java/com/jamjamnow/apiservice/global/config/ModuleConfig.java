package com.jamjamnow.apiservice.global.config;

import com.jamjamnow.commonmodule.config.CommonModuleConfig;
import com.jamjamnow.persistencemodule.global.config.PersistenceModuleConfig;
import com.jamjamnow.securitymodule.config.SecurityModuleConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    CommonModuleConfig.class,
    PersistenceModuleConfig.class,
    SecurityModuleConfig.class
})
public class ModuleConfig {

}
