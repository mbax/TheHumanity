package org.royaldev.thehumanity.cards.packs;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.royaldev.thehumanity.cards.Card;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of cards, generally with a common, underlying theme.
 */
public class MemoryCardPack implements CardPack {

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
    public MemoryCardPack(@NotNull final String name) {
        Preconditions.checkNotNull(name, "name was null");
        this.name = name;
    }

    /**
     * Adds a card to this pack. Only {@link WhiteCard WhiteCards} and {@link BlackCard BlackCards} are accepted.
     *
     * @param c Card to add
     * @throws IllegalArgumentException If card wasn't a white card or black card.
     * @throws IllegalArgumentException If card didn't belong to this pack
     */
    @Override
    public void addCard(@NotNull final Card c) {
        Preconditions.checkNotNull(c, "c was null");
        if (!c.getCardPack().equals(this)) {
            throw new IllegalArgumentException("Card did not belong to this CardPack");
        }
        if (c instanceof BlackCard) this.blackCards.add((BlackCard) c);
        else if (c instanceof WhiteCard) this.whiteCards.add((WhiteCard) c);
        else throw new IllegalArgumentException("Unknown card type!");
    }

    /**
     * Gets the author of this pack. If no author was specified in the pack's metadata, this will return null.
     *
     * @return Name of author or null
     */
    @Override
    @Nullable
    public String getAuthor() {
        return this.author;
    }

    /**
     * Sets the author of this pack.
     *
     * @param author New author
     */
    @Override
    public void setAuthor(@Nullable final String author) {
        this.author = author;
    }

    /**
     * Gets all the black cards contained in this pack. Note that the list returned is a clone. Modifying it will not
     * modify the pack.
     *
     * @return Cloned list of black cards
     */
    @Override
    @NotNull
    public List<BlackCard> getBlackCards() {
        return new ArrayList<>(this.blackCards);
    }

    /**
     * Gets the description of this pack. If no description was set in the pack's metadata, this will return null.
     *
     * @return Description of pack or null
     */
    @Override
    @Nullable
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of this pack.
     *
     * @param description New description
     */
    @Override
    public void setDescription(@Nullable final String description) {
        this.description = description;
    }

    /**
     * Gets the name of this pack
     *
     * @return Name
     */
    @Override
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Gets all the white cards contained in this pack. Note that the list return is a clone. Modifying it will not
     * modify the pack.
     *
     * @return Cloned list of white cards
     */
    @Override
    @NotNull
    public List<WhiteCard> getWhiteCards() {
        return new ArrayList<>(this.whiteCards);
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .omitNullValues()
            .add("name", this.name)
            .add("description", this.description)
            .add("author", this.author)
            .toString();
    }
}
