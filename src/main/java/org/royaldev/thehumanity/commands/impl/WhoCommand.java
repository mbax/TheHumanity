package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.Round;
import org.royaldev.thehumanity.Round.RoundStage;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.player.Player;

public class WhoCommand implements NoticeableCommand {

    private final TheHumanity humanity;

    public WhoCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    @Override
    public String getDescription() {
        return "Shows the players in this game and their status.";
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
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        final User u = event.getUser();
        final Game g = this.humanity.getGameFor(u);
        if (g == null) {
            this.notice(u, "You're not in a game.");
            return;
        }
        final Round r = g.getCurrentRound();
        final StringBuilder sb = new StringBuilder();
        if (r.getCurrentStage() != RoundStage.WAITING_FOR_CZAR && r.getCurrentStage() != RoundStage.WAITING_FOR_PLAYERS) {
            this.notice(u, "The game has not yet started.");
            return;
        }
        sb.append(Colors.BOLD).append("Czar: ").append(Colors.NORMAL).append(r.getCzar().getUser().getNick()).append(", ").append(Colors.BOLD).append("Players: ").append(Colors.NORMAL);
        for (final Player player : g.getPlayers()) {
            if (player.equals(r.getCzar())) continue;
            final String c = r.hasPlayed(player) ? Colors.GREEN : r.getCurrentStage() == RoundStage.WAITING_FOR_CZAR ? Colors.BLUE : Colors.RED;
            sb.append(c).append(player.getUser().getNick()).append(Colors.NORMAL).append(", ");
        }
        event.respond(g.antiPing(sb.substring(0, sb.length() - 2)));
    }
}
