package com.friendly.services.infrastructure.config.provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

/**
 * Provides ObjectMapper, configured to use Java Time module to work with date/time API.
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public class ObjectMapperProvider {

    private ObjectMapperProvider() {
    }

    public static ObjectMapper getObjectMapper() {

        final ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.findAndRegisterModules()
                           .registerModule(new JavaTimeModule())
                           .setSerializationInclusion(NON_NULL)
                           .setSerializationInclusion(NON_EMPTY)
                           .setSerializationInclusion(NON_ABSENT)
                           .enable(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING)
                           .enable(MapperFeature.USE_ANNOTATIONS)
                            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                           .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                           .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                           .setAnnotationIntrospector(new JacksonAnnotationIntrospector());
    }
}
