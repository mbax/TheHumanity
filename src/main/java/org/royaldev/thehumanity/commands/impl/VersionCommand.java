package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.util.ConversionHelper;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Command(
    name = "version",
    description = "Shows the version of the bot."
)
public class VersionCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public VersionCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Nullable
    private Manifest getManifest() {
        final Class<?> clazz = this.humanity.getClass();
        final String className = clazz.getSimpleName() + ".class";
        final String classPath = clazz.getResource(className).toString();
        final String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
        try {
            return new Manifest(new URL(manifestPath).openStream());
        } catch (final IOException ex) {
            return null;
        }
    }

    @NotNull
    private String getVersion() {
        final Manifest mf = this.getManifest();
        if (mf == null) return "Error: null Manifest";
        final Attributes a = mf.getAttributes("Version-Info");
        if (a == null) return "Error: No Version-Info";
        return String.format(
            "%s %s (%s)",
            a.getValue("Project-Name"),
            a.getValue("Project-Version"),
            a.getValue("Git-Describe")
        );
    }

    @Override
    public void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        final String version = this.getVersion();
        if (version.startsWith("Error:")) {
            this.notice(event.getActor(), version);
        } else {
            ConversionHelper.respond(event, version);
        }
    }
}
