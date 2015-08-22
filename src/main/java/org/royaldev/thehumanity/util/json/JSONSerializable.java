package org.royaldev.thehumanity.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public interface JSONSerializable {

    @NotNull
    default String toJSON() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (final JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

}
