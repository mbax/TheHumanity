package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.util.ConversionHelper;

@Command(
    name = "version",
    description = "Shows the version of the bot."
)
public class VersionCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public VersionCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        final String version = this.humanity.getVersion();
        if (version.startsWith("Error:")) {
            this.notice(event.getActor(), version);
        } else {
            ConversionHelper.respond(event, version);
        }
    }
}
