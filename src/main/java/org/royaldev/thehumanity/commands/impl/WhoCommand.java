package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.Game.Status;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;

public class WhoCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public WhoCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        final User u = event.getUser();
        final Game g = this.humanity.getGameFor(u);
        if (g == null) {
            this.notice(u, "You're not in a game.");
            return;
        }
        final StringBuilder sb = new StringBuilder();
        if (g.getStatus() != Status.WAITING_FOR_CZAR && g.getStatus() != Status.WAITING_FOR_PLAYERS) {
            this.notice(u, "The game has not yet started.");
            return;
        }
        sb.append(Colors.BOLD).append("Czar: ").append(Colors.NORMAL).append(g.getCzar().getNick()).append(", ").append(Colors.BOLD).append("Players: ").append(Colors.NORMAL);
        for (final User player : g.getUsers()) {
            if (player.equals(g.getCzar())) continue;
            final String c = g.hasPlayed(player) ? Colors.GREEN : g.getStatus() == Status.WAITING_FOR_CZAR ? Colors.BLUE : Colors.RED;
            sb.append(c).append(player.getNick()).append(Colors.NORMAL).append(", ");
        }
        event.respond(g.antiPing(sb.substring(0, sb.length() - 2)));
    }

    @Override
    public String getName() {
        return "who";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public String getDescription() {
        return "Shows the players in this game and their status.";
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
