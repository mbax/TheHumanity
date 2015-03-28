package org.royaldev.thehumanity.player;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.element.User;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;

import java.util.Collections;
import java.util.Set;

/**
 * Represents a player in one game of Cards Against Humanity.
 */
public class Player {

    private final Hand<WhiteCard> hand = new Hand<>();
    private final Multiset<BlackCard> wins = HashMultiset.create();
    private User user;

    /**
     * Creates a new player, with a backing user.
     *
     * @param user User that backs this player
     */
    public Player(@Nullable final User user) {
        this.user = user;
    }

    /**
     * Adds a win to this player's wins. This should always be used to add a winning card.
     *
     * @param win Card that won a point for this player
     */
    public void addWin(@NotNull final BlackCard win) {
        Preconditions.checkNotNull(win, "win was null");
        this.wins.add(win);
    }

    /**
     * Removes all wins from this player.
     */
    public void clearWins() {
        this.wins.clear();
    }

    /**
     * Checks equality with another player. This will return true if and only if the objects are the same object or have
     * the same (case-insensitive) nickname.
     *
     * @param obj Object to check equality with
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Player)) return false;
        final Player p = (Player) obj;
        return p.getUser().getNick().equalsIgnoreCase(this.getUser().getNick());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("hand", this.hand)
            .add("wins", this.wins)
            .add("user", this.user)
            .toString();
    }

    /**
     * Gets this player's hand of cards.
     *
     * @return Hand
     */
    @NotNull
    public Hand<WhiteCard> getHand() {
        return this.hand;
    }

    /**
     * Gets this player's current score, which is directly <code>this.getWins().size()</code>.
     *
     * @return Score
     */
    public int getScore() {
        return this.getWins().size();
    }

    /**
     * Gets the User backing this player.
     *
     * @return User
     */
    @NotNull
    public User getUser() {
        return this.user;
    }

    /**
     * Sets the User backing this player.
     *
     * @param user User
     */
    public void setUser(@Nullable final User user) {
        this.user = user;
    }

    /**
     * Returns an unmodifiable set of the cards representing points for this player.
     *
     * @return Unmodifiable set
     * @see Player#addWin(BlackCard)
     * @see Player#removeWin(BlackCard)
     */
    @NotNull
    public Set<BlackCard> getWins() {
        return Collections.unmodifiableSet(this.wins.elementSet());
    }

    /**
     * Removes a win from this player's wins. This should always be used to remove a winning card.
     *
     * @param win Card to remove
     */
    public void removeWin(@NotNull final BlackCard win) {
        Preconditions.checkNotNull(win, "win was null");
        this.wins.remove(win);
    }
}
