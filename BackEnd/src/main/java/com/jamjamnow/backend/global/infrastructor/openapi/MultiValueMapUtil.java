package com.jamjamnow.backend.global.infrastructor.openapi;

import java.util.Map;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class MultiValueMapUtil {

    public static MultiValueMap<String, String> fromSingleValueMap(Map<String, String> map) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        map.forEach(multiValueMap::add);
        return multiValueMap;
    }
}
