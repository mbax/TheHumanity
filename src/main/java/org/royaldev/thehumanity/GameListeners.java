package org.royaldev.thehumanity;

import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelKickEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.client.library.event.user.UserNickChangeEvent;
import org.kitteh.irc.client.library.event.user.UserQuitEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;
import org.royaldev.thehumanity.game.Game;
import org.royaldev.thehumanity.game.round.CurrentRound;
import org.royaldev.thehumanity.player.Player;

public class GameListeners {

    private final TheHumanity humanity;

    public GameListeners(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Handler
    public void onKick(final ChannelKickEvent event) {
        final User u = event.getActor();
        final Game g = this.humanity.getGameFor(u);
        if (g == null || !g.getChannel().getName().equalsIgnoreCase(event.getChannel().getName())) return;
        final Player p = g.getPlayer(event.getActor());
        if (p == null) return;
        g.removePlayer(p);
    }

    @Handler
    public void onNick(final UserNickChangeEvent event) {
        this.humanity.getGames().values().stream().forEach(
            game -> {
                game.updateChannel();
                game.getHistoricPlayers().stream()
                    .filter(p -> p.getUser().getNick().equals(event.getActor().getNick()))
                    .forEach(p -> p.setUser(event.getNewUser()));
            }
        );
    }

    @Handler
    public void onPart(final ChannelPartEvent event) {
        final User u = event.getActor();
        final Game g = this.humanity.getGameFor(u);
        if (g == null || !g.getChannel().getName().equalsIgnoreCase(event.getChannel().getName())) return;
        final Player p = g.getPlayer(event.getActor());
        if (p == null) return;
        g.removePlayer(p);
    }

    @Handler
    public void onQuit(final UserQuitEvent event) {
        final User u = event.getActor();
        final Game g = this.humanity.getGameFor(u);
        if (g == null) return;
        final Player p = g.getPlayer(u);
        if (p == null) return;
        g.removePlayer(p);
    }

    @Handler
    public void reminderTaskCanceller(final ChannelMessageEvent event) {
        final User u = event.getActor();
        final Game g = this.humanity.getGameFor(u);
        if (g == null) return;
        final Player p = g.getPlayer(u);
        final CurrentRound r = g.getCurrentRound();
        if (p == null || r == null || !p.equals(r.getCzar())) return;
        // This can't prematurely cancel, since the task is only made when the stage switches to waiting for czar.
        r.cancelReminderTask();
    }
}
