package com.jamjamnow.commonmodule.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jasypt.encryptor")
public record JasyptProperties(
    String key
) {

}
