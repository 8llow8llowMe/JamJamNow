package com.jamjamnow.backend.global.infrastructor.openapi;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class NumericStringToInt extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String raw = p.getValueAsString();

        if (raw == null || raw.isBlank()) {
            return 0;
        }
        return Integer.parseInt(raw.trim());
    }
}
