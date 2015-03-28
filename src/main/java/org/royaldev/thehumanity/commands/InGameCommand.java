package org.royaldev.thehumanity.commands;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.player.Player;

/**
 * A command that is only meant to be used when the user is in a game.
 */
public abstract class InGameCommand extends NoticeableCommand {

    protected final TheHumanity humanity;

    public InGameCommand(@NotNull final TheHumanity instance) {
        Preconditions.checkNotNull(instance, "instance was null");
        this.humanity = instance;
    }

    /**
     * This method is called after checking that the User performing the command is in a Game. The Game that the User is
     * playing is then provided to this method.
     *
     * @param event  Event of receiving command
     * @param ci     Information received when this command was called
     * @param game   The game the User is in
     * @param player Player that used the command
     * @param args   Arguments passed to the command
     */
    public abstract void onInGameCommand(@NotNull final ActorEvent<User> event, final CallInfo ci, @NotNull final Game game, @NotNull final Player player, @NotNull final String[] args);

    /**
     * Gets the Game for the given User.
     *
     * @param u User to get Game of
     * @return Game
     */
    @Nullable
    public Game getGame(@NotNull final User u) {
        Preconditions.checkNotNull(u, "u was null");
        return this.humanity.getGameFor(u);
    }

    /**
     * Checks to see if the given Player is the host or a channel operator in the given Game.
     *
     * @param p Player to check
     * @param g Game to check
     * @return true if host or chanop, false if not
     */
    @Contract("null, _ -> false; _, null -> false; !null, !null -> _")
    public boolean isHostOrOp(final Player p, final Game g) {
        return !(p == null || g == null) && (g.getHost().equals(p) || this.humanity.hasChannelMode(g.getChannel(), p.getUser(), 'o'));
    }

    /**
     * Checks to see if the given Player is the host or a channel operator in the game the Player is a part of.
     *
     * @param p Player to check
     * @return true if host or chanop, false if not
     */
    @Contract("null -> false")
    public boolean isHostOrOp(final Player p) {
        return this.isHostOrOp(p, this.getGame(p.getUser()));
    }

    /**
     * Checks to see if the given User is part of a Game.
     *
     * @param u User to check
     * @return true if in a Game, false if otherwise
     */
    @Contract("null -> fail")
    public boolean isInGame(@NotNull final User u) {
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
    public final void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        final Game g = this.getGame(event.getActor());
        final Player p;
        if (g == null || (p = g.getPlayer(event.getActor())) == null) {
            this.notice(event.getActor(), "You're not in a game.");
            return;
        }
        this.onInGameCommand(event, ci, g, p, args);
    }
}
