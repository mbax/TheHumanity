package org.royaldev.thehumanity.cards;

public abstract class Card {

    protected final CardPack cardPack;
    protected final String rawText;
    protected final String processedText;

    protected Card(CardPack cardPack, String rawText) {
        this.cardPack = cardPack;
        this.rawText = rawText;
        this.processedText = this.processText(this.rawText);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Card)) return false;
        final Card c = (Card) obj;
        return this.getText().equals(c.getText()) && this.getCardPack().equals(c.getCardPack());
    }

    @Override
    public String toString() {
        return this.getText();
    }

    public CardPack getCardPack() {
        return this.cardPack;
    }

    public String getText() {
        return this.processedText;
    }

    protected abstract String processText(final String rawText);

}
