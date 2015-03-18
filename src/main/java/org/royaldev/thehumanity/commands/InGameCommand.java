package org.royaldev.thehumanity.commands;

import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;

/**
 * A command that is only meant to be used when the user is in a game.
 */
public abstract class InGameCommand extends NoticeableCommand {

    protected final TheHumanity humanity;

    public InGameCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    /**
     * This method is called after checking that the User performing the command is in a Game. The Game that the User is
     * playing is then provided to this method.
     *
     * @param event Event of receiving command
     * @param ci    Information received when this command was called
     * @param g     The game the User is in
     * @param args  Arguments passed to the command
     */
    public abstract void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, final Game g, final String[] args);

    /**
     * Gets the Game for the given User.
     *
     * @param u User to get Game of
     * @return Game
     */
    public Game getGame(final User u) {
        return this.humanity.getGameFor(u);
    }

    /**
     * Checks to see if the given User is part of a Game.
     *
     * @param u User to check
     * @return true if in a Game, false if otherwise
     */
    public boolean isInGame(final User u) {
        return this.getGame(u) != null;
    }

    /**
     * Checks to see if the User performing this command is in a Game. If not, the User will be noticed and the method
     * will return. If the User is in a game, {@link #onInGameCommand onInGameCommand()} is called.
     *
     * @param event Event of receiving command
     * @param ci    Information received when this command was called
     * @param args  Arguments passed to the command
     */
    @Override
    public final void onCommand(final ActorEvent<User> event, final CallInfo ci, final String[] args) {
        final Game g = this.getGame(event.getActor());
        if (g == null) {
            this.notice(event.getActor(), "You're not in a game.");
            return;
        }
        this.onInGameCommand(event, ci, g, args);
    }
}
