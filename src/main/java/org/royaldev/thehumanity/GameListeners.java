package org.royaldev.thehumanity;

import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelKickEvent;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.client.library.event.user.UserNickChangeEvent;
import org.kitteh.irc.client.library.event.user.UserQuitEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;

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
        g.removePlayer(g.getPlayer(event.getActor()));
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
        g.removePlayer(g.getPlayer(event.getActor()));
    }

    @Handler
    public void onQuit(final UserQuitEvent event) {
        final User u = event.getActor();
        final Game g = this.humanity.getGameFor(u);
        if (g == null) return;
        g.removePlayer(g.getPlayer(u));
    }
}
