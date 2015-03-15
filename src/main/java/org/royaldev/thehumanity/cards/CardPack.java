package org.royaldev.thehumanity.cards;

import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of cards, generally with a common, underlying theme.
 */
public class CardPack {

    private final String name;
    private final List<BlackCard> blackCards = new ArrayList<>();
    private final List<WhiteCard> whiteCards = new ArrayList<>();
    private String description;
    private String author;

    /**
     * Constructs a new pack with the given name.
     *
     * @param name Name of the pack
     */
    public CardPack(final String name) {
        this.name = name;
    }

    /**
     * Gets the name of a pack from a file name.
     *
     * @param fileName File name
     * @return Pack name
     */
    public static String getNameFromFileName(final String fileName) {
        final String[] split = fileName.split("\\.");
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) sb.append(split[i]);
        return sb.toString();
    }

    /**
     * Adds a card to this pack. Only {@link WhiteCard WhiteCards} and {@link BlackCard BlackCards} are accepted.
     *
     * @param c Card to add
     * @throws java.lang.IllegalArgumentException If card wasn't a white card or black card.
     */
    public void addCard(final Card c) {
        if (c instanceof BlackCard) this.blackCards.add((BlackCard) c);
        else if (c instanceof WhiteCard) this.whiteCards.add((WhiteCard) c);
        else throw new IllegalArgumentException("Unknown card type!");
    }

    /**
     * Checks to see if the two packs have the same name.
     *
     * @param obj Other pack
     * @return true if equal, false if otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CardPack)) return false;
        final CardPack cp = (CardPack) obj;
        return cp.getName().equals(this.getName());
    }

    /**
     * Gets the author of this pack. If no author was specified in the pack's metadata, this will return null.
     *
     * @return Name of author or null
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Sets the author of this pack.
     *
     * @param author New author
     */
    public void setAuthor(final String author) {
        this.author = author;
    }

    /**
     * Gets all the black cards contained in this pack. Note that the list returned is a clone. Modifying it will not
     * modify the pack.
     *
     * @return Cloned list of black cards
     */
    public List<BlackCard> getBlackCards() {
        return new ArrayList<>(this.blackCards);
    }

    /**
     * Gets the description of this pack. If no description was set in the pack's metadata, this will return null.
     *
     * @return Description of pack or null
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of this pack.
     *
     * @param description New description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the name of this pack
     *
     * @return Name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets all the white cards contained in this pack. Note that the list return is a clone. Modifying it will not
     * modify the pack.
     *
     * @return Cloned list of white cards
     */
    public List<WhiteCard> getWhiteCards() {
        return new ArrayList<>(this.whiteCards);
    }
}
