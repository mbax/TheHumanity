package org.royaldev.thehumanity.server.controllers;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.json.JSONWriter;

import java.io.StringWriter;

public final class APIHelper {

    public static String makeError(@NotNull final Object error) {
        Preconditions.checkNotNull(error, "error was null");
        final StringWriter sw = new StringWriter();
        new JSONWriter(sw).object().key("error").value(error).endObject();
        return sw.toString();
    }

}
