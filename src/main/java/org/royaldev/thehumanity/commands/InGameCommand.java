package org.royaldev.thehumanity.commands;

import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;

public abstract class InGameCommand extends NoticeableCommand {

    protected final TheHumanity humanity;

    protected InGameCommand(final TheHumanity humanity) {
        this.humanity = humanity;
    }

    public abstract void onInGameCommand(final GenericMessageEvent event, final CallInfo ci, final Game g, final String[] args);

    public Game getGame(final User u) {
        return this.humanity.getGameFor(u);
    }

    public boolean isInGame(final User u) {
        return this.getGame(u) != null;
    }

    @Override
    public final void onCommand(final GenericMessageEvent event, final CallInfo ci, final String[] args) {
        final Game g = this.getGame(event.getUser());
        if (g == null) {
            this.notice(event.getUser(), "You're not in a game.");
            return;
        }
        this.onInGameCommand(event, ci, g, args);
    }
}
