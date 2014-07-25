package org.royaldev.thehumanity.cards;

import org.pircbotx.Colors;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Play {

    private final Player player;
    private final List<WhiteCard> whiteCards = new ArrayList<>();

    public Play(final Player player, final List<WhiteCard> whiteCards) {
        this.player = player;
        this.whiteCards.addAll(whiteCards);
    }

    public Player getPlayer() {
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
