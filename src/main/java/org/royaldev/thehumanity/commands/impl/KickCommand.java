package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;

public class KickCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public KickCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        final User u = event.getUser();
        if (args.length < 1) {
            this.notice(u, "Usage: " + this.getUsage().replace("<command>", ci.getLabel()));
            return;
        }
        final Game g = this.humanity.getGameFor(u);
        if (g == null) {
            this.notice(u, "You're not in a game.");
            return;
        }
        if (!g.getChannel().getOps().contains(u) && !g.getHost().equals(u)) {
            this.notice(u, "You're not an op or the host!");
            return;
        }
        g.removeUser(args[0]);
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getUsage() {
        return "<command> [player]";
    }

    @Override
    public String getDescription() {
        return "Kicks a player from the game.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"k"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }
}
