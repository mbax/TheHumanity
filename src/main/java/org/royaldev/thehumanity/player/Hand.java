package org.royaldev.thehumanity.player;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.royaldev.thehumanity.cards.Card;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a hand of a player. The player has no association with this class.
 */
public class Hand<T extends Card> implements Iterable<T> {

    private final List<T> hand = Collections.synchronizedList(new ArrayList<>());

    /**
     * Adds a card into this hand.
     *
     * @param card Card to add
     */
    public void addCard(@NotNull final T card) {
        Preconditions.checkNotNull(card, "card was null");
        synchronized (this.hand) {
            this.hand.add(card);
        }
    }

    /**
     * Adds a Collection of cards into this hand.
     *
     * @param cards Cards to add
     */
    public void addCards(@NotNull final Collection<? extends T> cards) {
        Preconditions.checkNotNull(cards, "cards was null");
        synchronized (this.hand) {
            this.hand.addAll(cards);
        }
    }

    /**
     * Clears this hand by removing all of the cards.
     */
    public void clearHand() {
        synchronized (this.hand) {
            this.hand.clear();
        }
    }

    /**
     * Gets the card at the given index. This takes a String with the given index + 1. So, if the given String is "5",
     * the card at index 4 will be retrieved.
     *
     * @param indexString Index of the card (+1)
     * @return Card
     * @throws java.lang.NumberFormatException If the given string isn't a number
     */
    @NotNull
    public T getCard(@NotNull final String indexString) {
        Preconditions.checkNotNull(indexString, "indexString was null");
        final int index;
        try {
            index = Integer.parseInt(indexString);
        } catch (NumberFormatException ex) {
            throw new NumberFormatException(ex.getMessage());
        }
        return this.getCard(index - 1);
    }

    /**
     * Gets the card at the given index. If the index is invalid, an IllegalArgumentException will be thrown.
     *
     * @param index Index of the card
     * @return Card
     * @throws java.lang.IllegalArgumentException If the index is invalid
     */
    @NotNull
    public T getCard(final int index) {
        synchronized (this.hand) {
            if (index < 0 || index >= this.hand.size()) {
                throw new IllegalArgumentException("The given index was invalid.");
            }
            return this.hand.get(index);
        }
    }

    /**
     * Gets all of the cards in this hand.
     *
     * @return Cloned list of cards
     */
    @NotNull
    public List<T> getCards() {
        synchronized (this.hand) {
            return new ArrayList<>(this.hand);
        }
    }

    /**
     * Gets the amount of cards in this hand.
     *
     * @return int
     */
    public int getSize() {
        synchronized (this.hand) {
            return this.hand.size();
        }
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        synchronized (this.hand) {
            return hand.iterator();
        }
    }

    /**
     * Removes a card from this hand.
     *
     * @param card Card to remove
     * @return true if successful, false if otherwise
     */
    public boolean removeCard(@NotNull final T card) {
        Preconditions.checkNotNull(card, "card was null");
        synchronized (this.hand) {
            return this.hand.remove(card);
        }
    }

    /**
     * Removes a Collection of cards from this hand.
     *
     * @param cards Cards to remove
     * @return true if the hand changed, false if otherwise
     */
    public boolean removeCards(@NotNull final Collection<? extends T> cards) {
        Preconditions.checkNotNull(cards, "cards was null");
        synchronized (this.hand) {
            return this.hand.removeAll(cards);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("hand", this.hand)
            .toString();
    }
}
