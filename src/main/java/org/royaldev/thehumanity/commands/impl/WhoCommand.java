package org.royaldev.thehumanity.commands.impl;

import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.Round;
import org.royaldev.thehumanity.Round.RoundStage;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Player;
import org.royaldev.thehumanity.util.ConversionHelper;

@Command(
    name = "who",
    description = "Shows the players in this game and their status."
)
public class WhoCommand extends InGameCommand {

    public WhoCommand(final TheHumanity instance) {
        super(instance);
    }

    /**
     * Builds a String containing information about who the czar is, who is playing, and if they have played or not.
     *
     * @param g Game to build this String for
     * @return Built String, ready to be sent
     */
    private String getStatusString(final Game g) {
        final Round r = g.getCurrentRound();
        final StringBuilder sb = new StringBuilder();
        sb.append(IRCFormat.BOLD).append("Czar: ").append(IRCFormat.RESET).append(r.getCzar().getUser().getNick()).append(", ").append(IRCFormat.BOLD).append("Players: ").append(IRCFormat.RESET);
        for (final Player player : g.getPlayers()) {
            if (player.equals(r.getCzar())) continue;
            final String c = (r.hasPlayed(player) ? IRCFormat.GREEN : r.getCurrentStage() == RoundStage.WAITING_FOR_CZAR ? IRCFormat.BLUE : IRCFormat.RED).toString();
            sb.append(c).append(player.getUser().getNick()).append(IRCFormat.RESET).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    @Override
    public void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, final Game g, final String[] args) {
        final User u = event.getActor();
        final Round r = g.getCurrentRound();
        if (r == null || r.getCurrentStage() != RoundStage.WAITING_FOR_CZAR && r.getCurrentStage() != RoundStage.WAITING_FOR_PLAYERS) {
            this.notice(u, "The game has not yet started.");
            return;
        }
        final String ss = this.getStatusString(g);
        ConversionHelper.respond(event, g.antiPing(ss));
    }
}
