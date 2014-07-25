package org.royaldev.thehumanity.cards.types;

import org.pircbotx.Colors;
import org.royaldev.thehumanity.cards.Card;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.cards.Play;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlackCard extends Card {

    private final static Pattern blankPattern = Pattern.compile("((?<!\\\\)_)");

    public BlackCard(CardPack cardPack, String rawText) {
        super(cardPack, rawText);
    }

    private int countMatches(Pattern p, String toCount) {
        final Matcher m = p.matcher(toCount);
        int count = 0;
        while (m.find()) count++;
        return count;
    }

    public String fillInBlanks(final Play p) {
        String filled = this.rawText;
        for (final WhiteCard wc : p.getWhiteCards()) {
            final Matcher m = BlackCard.blankPattern.matcher(filled);
            filled = m.replaceFirst(Colors.BOLD + wc.getText() + Colors.NORMAL);
        }
        return filled;
    }

    public int getBlanks() {
        return this.countMatches(BlackCard.blankPattern, this.rawText);
    }

    @Override
    protected String processText(String rawText) {
        return BlackCard.blankPattern.matcher(rawText).replaceAll("<BLANK>").replace("\\_", "_");
    }
}
