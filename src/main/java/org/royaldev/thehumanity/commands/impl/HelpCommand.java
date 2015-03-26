package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.IRCCommand;
import org.royaldev.thehumanity.commands.NoticeableCommand;

import java.util.stream.Collectors;

@Command(
    name = "help",
    description = "Gets the help for all commands."
)
public class HelpCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public HelpCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    /**
     * Gets the names of all commands, in alphabetical order, concatenated together with no delimiter. Used for a cache
     * String by the {@link org.royaldev.thehumanity.TheHumanity#gist TheHumanity#gist()} method.
     *
     * @return String, as specified above
     */
    private String getNames() {
        return this.humanity.getCommandHandler().getAll().stream().map(IRCCommand::getName).sorted().collect(Collectors.joining());
    }

    @Override
    public void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        final User u = event.getActor();
        final StringBuilder sb = new StringBuilder();
        for (final IRCCommand ic : this.humanity.getCommandHandler().getAll()) {
            sb.append("## ").append(this.humanity.getPrefix()).append(ic.getName()).append("\n");
            sb.append("*").append(ic.getDescription()).append("*  \n");
            if (!"<command>".equalsIgnoreCase(ic.getUsage())) {
                sb.append("**Usage:** ").append(ic.getUsage().replace("<command>", ic.getName())).append("  \n");
            }
            if (ic.getAliases().length > 0) {
                sb.append("**Aliases:** ").append(String.join(", ", ic.getAliases())).append("\n");
            }
        }
        this.notice(u, this.humanity.gist("help", this.getNames(), "help.md", sb.toString()));
    }
}
