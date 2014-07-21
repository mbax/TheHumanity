package org.royaldev.thehumanity;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

public class GameListeners extends ListenerAdapter<PircBotX> {

    private final TheHumanity humanity;

    public GameListeners(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public void onPart(PartEvent<PircBotX> event) throws Exception {
        final User u = event.getUser();
        final Game g = this.humanity.getGameFor(u);
        if (g == null || !g.hasUser(u)) return;
        g.removeUser(event.getUser());
    }

    @Override
    public void onQuit(QuitEvent<PircBotX> event) throws Exception {
        this.onPart(new PartEvent<>(event.getBot(), event.getDaoSnapshot(), null, event.getUser(), event.getReason()));
    }
}
