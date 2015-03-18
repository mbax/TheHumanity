package org.royaldev.thehumanity;

import org.kitteh.irc.client.library.EventHandler;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.client.library.event.user.UserQuitEvent;

public class GameListeners {

    private final TheHumanity humanity;

    public GameListeners(final TheHumanity instance) {
        this.humanity = instance;
    }

    @EventHandler
    public void onPart(final ChannelPartEvent event) {
        final User u = event.getActor();
        final Game g = this.humanity.getGameFor(u);
        if (g == null || !g.getChannel().getName().equalsIgnoreCase(event.getChannel().getName())) return;
        g.removePlayer(g.getPlayer(event.getActor()));
    }

    @EventHandler
    public void onQuit(final UserQuitEvent event) {
        final User u = event.getActor();
        final Game g = this.humanity.getGameFor(u);
        if (g == null) return;
        g.removePlayer(g.getPlayer(u));
    }
}
