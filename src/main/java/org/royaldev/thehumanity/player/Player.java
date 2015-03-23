package org.royaldev.thehumanity.player;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;

/**
 * Represents a player in one game of Cards Against Humanity.
 */
public class Player {

    private final Hand<WhiteCard> hand = new Hand<>();
    private final Hand<BlackCard> wins = new Hand<>();
    private User user;

    public Player(final User user) {
        this.user = user;
    }

    public void addWin(@NotNull final BlackCard win) {
        this.getWins().addCard(win);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Player)) return false;
        final Player p = (Player) obj;
        return p.getUser().getNick().equalsIgnoreCase(this.getUser().getNick());
    }

    @NotNull
    public Hand<WhiteCard> getHand() {
        return this.hand;
    }

    public int getScore() {
        return this.getWins().getSize();
    }

    @NotNull
    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    @NotNull
    public Hand<BlackCard> getWins() {
        return this.wins;
    }
}
