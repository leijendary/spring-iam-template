package com.leijendary.spring.iamtemplate.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static org.apache.commons.compress.utils.Sets.newHashSet;

@JsonComponent
public class StringTrimDeserializer extends StringDeserializer {

    private static final Set<String> SKIP_FIELDS = newHashSet("password");

    @Override
    public String deserialize(final JsonParser jsonParser,
                              final DeserializationContext deserializationContext) throws IOException {
        // Get the result from the original StringDeserializer
        final var deserialized = super.deserialize(jsonParser, deserializationContext);
        final var fieldName = jsonParser.getParsingContext().getCurrentName();

        // If this field is included in the fields to be skipped, just skip
        if (SKIP_FIELDS.contains(fieldName)) {
            return deserialized;
        }

        // ... and just trim it
        return ofNullable(deserialized)
                .map(String::trim)
                .orElse(deserialized);
    }
}
