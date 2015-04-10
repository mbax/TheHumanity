package org.royaldev.thehumanity.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.json.JSONWriter;
import org.royaldev.thehumanity.util.ThrowingFunction;

import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.function.Consumer;

public final class APIHelper {

    public static final String PRODUCES = "application/json; charset=utf-8";

    public static String makeError(@NotNull final Object error) {
        Preconditions.checkNotNull(error, "error was null");
        final StringWriter sw = new StringWriter();
        new JSONWriter(sw).object().key("error").value(error).endObject();
        return sw.toString();
    }

    public static String makeJSON(@NotNull final Consumer<JSONWriter> consumer) {
        Preconditions.checkNotNull(consumer, "consumer was null");
        final StringWriter sw = new StringWriter();
        final JSONWriter jw = new JSONWriter(sw);
        consumer.accept(jw);
        return sw.toString();
    }

    public static String makeObjectMapperJSON(@NotNull final ThrowingFunction<ObjectMapper, String> function) {
        Preconditions.checkNotNull(function, "function was null");
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return function.apply(objectMapper);
        } catch (final Exception ex) {
            return APIHelper.makeError(ex);
        }
    }

    public static String makeObjectMapperJSON(@NotNull final HttpServletResponse response, @NotNull final ThrowingFunction<ObjectMapper, String> function) {
        Preconditions.checkNotNull(response, "response was null");
        Preconditions.checkNotNull(function, "function was null");
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return function.apply(objectMapper);
        } catch (final Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return APIHelper.makeError(ex);
        }
    }

}
