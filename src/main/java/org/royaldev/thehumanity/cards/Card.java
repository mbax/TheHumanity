package org.royaldev.thehumanity.cards;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.royaldev.thehumanity.cards.packs.CardPack;

/**
 * A card in general.
 */
public abstract class Card {

    protected final CardPack cardPack;
    protected final String rawText;
    protected final String processedText;

    /**
     * Constructs a new card for the given pack with the given text.
     *
     * @param cardPack Pack the card belongs to
     * @param rawText  Text on the card
     */
    protected Card(@NotNull final CardPack cardPack, @NotNull final String rawText) {
        Preconditions.checkNotNull(cardPack, "cardPack was null");
        Preconditions.checkNotNull(rawText, "rawText was null");
        this.cardPack = cardPack;
        this.rawText = rawText;
        this.processedText = this.processText(this.rawText);
    }

    /**
     * Processes internal text for public viewing.
     *
     * @param rawText Internal text
     * @return Public-viewable text
     */
    @NotNull
    protected abstract String processText(@NotNull final String rawText);

    /**
     * Checks to see if the cards have the same text and belong to the same pack.
     *
     * @param obj Other card
     * @return true if equal, false if otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Card)) return false;
        final Card c = (Card) obj;
        return this.getText().equals(c.getText()) && this.getCardPack().equals(c.getCardPack());
    }

    /**
     * Returns {@link #getText}.
     *
     * @return {@link #getText}.
     */
    @Override
    @NotNull
    public String toString() {
        return this.getText();
    }

    /**
     * Gets the pack this card belongs to.
     *
     * @return Pack
     */
    @NotNull
    public CardPack getCardPack() {
        return this.cardPack;
    }

    /**
     * Gets the public-viewable text of this card.
     *
     * @return Text
     */
    @NotNull
    public String getText() {
        return this.processedText;
    }

}
