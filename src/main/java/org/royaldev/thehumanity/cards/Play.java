package org.royaldev.thehumanity.cards;

import org.pircbotx.Colors;
import org.pircbotx.User;
import org.royaldev.thehumanity.cards.Card.WhiteCard;

import java.util.ArrayList;
import java.util.List;

public class Play {

    private final User player;
    private final List<WhiteCard> whiteCards = new ArrayList<>();

    public Play(final User player, final List<WhiteCard> whiteCards) {
        this.player = player;
        this.whiteCards.addAll(whiteCards);
    }

    public User getPlayer() {
        return this.player;
    }

    public List<WhiteCard> getWhiteCards() {
        return this.whiteCards;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final WhiteCard wc : this.getWhiteCards()) {
            sb.append(Colors.BOLD).append(wc.getText()).append(Colors.NORMAL).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}
