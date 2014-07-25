package org.royaldev.thehumanity.cards;

import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.player.Hand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<CardPack> cardPacks = Collections.synchronizedList(new ArrayList<CardPack>());
    private final List<WhiteCard> whiteCards = Collections.synchronizedList(new ArrayList<WhiteCard>());
    private final List<BlackCard> blackCards = Collections.synchronizedList(new ArrayList<BlackCard>());

    public Deck(final Collection<CardPack> cardPacks) {
        synchronized (this.cardPacks) {
            this.cardPacks.addAll(cardPacks);
        }
        this.repopulateBlackCards();
        this.repopulateWhiteCards();
    }

    public List<CardPack> getCardPacks() {
        synchronized (this.cardPacks) {
            return new ArrayList<>(this.cardPacks);
        }
    }

    /**
     * Gets a random black card. If there are none left in this Deck, null will be returned. A manual repopulation can
     * be done using {@link #repopulateBlackCards()}, but the default game behavior is to end when black cards are
     * exhausted.
     *
     * @return A random black card or null
     */
    public BlackCard getRandomBlackCard() {
        synchronized (this.blackCards) {
            if (this.blackCards.size() < 1) return null;
            Collections.shuffle(this.blackCards);
            return this.blackCards.remove(0);
        }
    }

    /**
     * Gets a random white card. If there are none left in this Deck, the pile will be repopulated. An optional
     * parameter may be included to specify Hands containing cards that are not to be included when the pile is
     * repopulated. If it is null, the hand will be repopulated normally.
     *
     * @param repopulateExcludes Hands with cards not to include or null
     * @return A random white card
     */
    public WhiteCard getRandomWhiteCard(final Collection<Hand> repopulateExcludes) {
        synchronized (this.whiteCards) {
            if (this.whiteCards.size() < 1) this.repopulateWhiteCards(repopulateExcludes);
            Collections.shuffle(this.whiteCards);
            return this.whiteCards.remove(0);
        }
    }

    public void repopulateBlackCards() {
        for (final CardPack cp : this.cardPacks) {
            this.blackCards.addAll(cp.getBlackCards());
        }
    }

    public void repopulateWhiteCards(final Collection<Hand> exclude) {
        synchronized (this.cardPacks) {
            synchronized (this.whiteCards) {
                for (final CardPack cp : this.cardPacks) {
                    thisCard:
                    for (final WhiteCard wc : cp.getWhiteCards()) {
                        if (exclude != null && !exclude.isEmpty()) {
                            for (final Hand h : exclude) {
                                if (h.getCards().contains(wc)) continue thisCard;
                            }
                        }
                        this.whiteCards.add(wc);
                    }
                }
            }
        }
    }

    public void repopulateWhiteCards() {
        this.repopulateWhiteCards(null);
    }

}
