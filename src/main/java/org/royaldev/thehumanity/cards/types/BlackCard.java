package org.royaldev.thehumanity.cards.types;

import org.kitteh.irc.client.library.IRCFormat;
import org.royaldev.thehumanity.cards.Card;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.cards.Play;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A black card.
 */
public class BlackCard extends Card {

    private static final Pattern blankPattern = Pattern.compile("((?<!\\\\)_)");

    /**
     * Constructs a new black card for the given pack with the given text. All blanks should be one underscore ("_").
     * Multiple underscores will result in multiple blanks.
     *
     * @param cardPack Pack this card belongs to
     * @param rawText  Text this card has
     */
    public BlackCard(final CardPack cardPack, final String rawText) {
        super(cardPack, rawText);
    }

    /**
     * Counts the matches found by applying a pattern to a string.
     *
     * @param p       Pattern to apply
     * @param toCount String to count matche sin
     * @return Number of matches
     */
    private int countMatches(final Pattern p, final String toCount) {
        final Matcher m = p.matcher(toCount);
        int count = 0;
        while (m.find()) count++;
        return count;
    }

    /**
     * Returns a String with the blanks filled in by the given play.
     *
     * @param p Play containing cards to fill blanks in with.
     * @return Filled in black card text
     */
    public String fillInBlanks(final Play p) {
        String filled = this.rawText;
        for (final WhiteCard wc : p.getWhiteCards()) {
            final Matcher m = BlackCard.blankPattern.matcher(filled);
            try {
                filled = m.replaceFirst(IRCFormat.BOLD + wc.getText().replaceAll("([\\\\\\$])", "\\\\$1") + IRCFormat.RESET);
            } catch (final IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        }
        return filled;
    }

    /**
     * Gets the amount of blanks this card has.
     *
     * @return Number of blanks
     */
    public int getBlanks() {
        return this.countMatches(BlackCard.blankPattern, this.rawText);
    }

    /**
     * Processes this card's internal text for the user's viewing.
     *
     * @param rawText Internal text
     * @return Processed text
     */
    @Override
    protected String processText(final String rawText) {
        return BlackCard.blankPattern.matcher(rawText).replaceAll("<BLANK>").replace("\\_", "_");
    }
}
