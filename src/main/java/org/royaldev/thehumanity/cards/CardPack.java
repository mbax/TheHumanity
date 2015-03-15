package org.royaldev.thehumanity.cards;

import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;

import java.util.ArrayList;
import java.util.List;

public class CardPack {

    private final String name;
    private final List<BlackCard> blackCards = new ArrayList<>();
    private final List<WhiteCard> whiteCards = new ArrayList<>();
    private String description;
    private String author;

    public CardPack(final String name) {
        this.name = name;
    }

    public static String getNameFromFileName(final String fileName) {
        final String[] split = fileName.split("\\.");
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) sb.append(split[i]);
        return sb.toString();
    }

    public void addCard(final Card c) {
        if (c instanceof BlackCard) this.blackCards.add((BlackCard) c);
        else if (c instanceof WhiteCard) this.whiteCards.add((WhiteCard) c);
        else throw new IllegalArgumentException("Unknown card type!");
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CardPack)) return false;
        final CardPack cp = (CardPack) obj;
        return cp.getName().equals(this.getName());
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public List<BlackCard> getBlackCards() {
        return new ArrayList<>(this.blackCards);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public List<WhiteCard> getWhiteCards() {
        return new ArrayList<>(this.whiteCards);
    }
}
