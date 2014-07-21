package org.royaldev.thehumanity.cards;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;

public abstract class Card {

    final CardPack cardPack;
    final String rawText;
    final String processedText;

    private Card(CardPack cardPack, String rawText) {
        this.cardPack = cardPack;
        this.rawText = rawText;
        this.processedText = this.processText(this.rawText);
    }

    public String getText() {
        return this.processedText;
    }

    public CardPack getCardPack() {
        return this.cardPack;
    }

    abstract String processText(final String rawText);

    @Override
    public String toString() {
        return this.getText();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Card)) return false;
        final Card c = (Card) obj;
        return this.getText().equals(c.getText()) && this.getCardPack().equals(c.getCardPack());
    }

    public static class BlackCard extends Card {
        public BlackCard(CardPack cardPack, String rawText) {
            super(cardPack, rawText);
        }

        @Override
        String processText(String rawText) {
            return rawText.replaceAll("_", "<BLANK>");
        }

        public String fillInBlanks(final Play p) {
            String filled = this.rawText;
            for (final WhiteCard wc : p.getWhiteCards()) {
                filled = filled.replaceFirst("_", Colors.BOLD + wc.getText() + Colors.NORMAL);
            }
            return filled;
        }

        public int getBlanks() {
            return StringUtils.countMatches(this.rawText, "_");
        }
    }

    public static class WhiteCard extends Card {
        public WhiteCard(CardPack cardPack, String rawText) {
            super(cardPack, rawText);
        }

        @Override
        String processText(String rawText) {
            return rawText;
        }
    }
}
