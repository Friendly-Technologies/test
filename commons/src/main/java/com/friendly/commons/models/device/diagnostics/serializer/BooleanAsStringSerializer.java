package com.friendly.commons.models.device.diagnostics.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class BooleanAsStringSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if ("true".equals(value) || "false".equals(value)) {
            gen.writeBoolean(Boolean.parseBoolean(value));
        } else {
            gen.writeString(value);
        }
    }
}
