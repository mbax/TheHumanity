package org.royaldev.thehumanity.cards;

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
    protected Card(final CardPack cardPack, final String rawText) {
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
    protected abstract String processText(final String rawText);

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
    public String toString() {
        return this.getText();
    }

    /**
     * Gets the pack this card belongs to.
     *
     * @return Pack
     */
    public CardPack getCardPack() {
        return this.cardPack;
    }

    /**
     * Gets the public-viewable text of this card.
     *
     * @return Text
     */
    public String getText() {
        return this.processedText;
    }

}
