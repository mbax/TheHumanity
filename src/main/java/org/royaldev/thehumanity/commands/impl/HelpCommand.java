package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.IRCCommand;
import org.royaldev.thehumanity.commands.NoticeableCommand;

import java.util.Arrays;

public class HelpCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public HelpCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        final User u = event.getUser();
        if (!(event instanceof PrivateMessageEvent)) this.notice(u, "Check your private messages.");
        for (final IRCCommand ic : this.humanity.getCommandHandler().getAll()) {
            u.send().message(Colors.BOLD + this.humanity.getPrefix() + ic.getName() + Colors.NORMAL+ " – " + ic.getDescription());
            u.send().message("  Usage: " + ic.getUsage().replaceAll("<command>", ic.getName()));
            u.send().message("  Aliases: " + Arrays.toString(ic.getAliases()));
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public String getDescription() {
        return "Gets the help for all commands.";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }
}
