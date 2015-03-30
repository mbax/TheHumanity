package org.royaldev.thehumanity.player;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.royaldev.thehumanity.CardHelper;
import org.royaldev.thehumanity.ReflectiveToStringHelper;
import org.royaldev.thehumanity.ReflectiveToStringHelper.Include;
import org.royaldev.thehumanity.cards.types.WhiteCard;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HandTest {

    private Hand<WhiteCard> hand;

    @Before
    public void setUp() throws Exception {
        this.hand = new Hand<>();
    }

    @After
    public void tearDown() throws Exception {
        this.hand = null;
    }

    @Test
    public void testAddCard() throws Exception {
        // Should start with no cards
        assertSame(0, this.hand.getSize());
        // Make and add a card
        final WhiteCard wc = CardHelper.makeWhiteCard("Zarthus");
        this.hand.addCard(wc);
        // Should have one card now
        assertSame(1, this.hand.getSize());
        // The cards should match
        assertTrue(this.hand.getCard(0).equals(wc));
    }

    @Test
    public void testAddCards() throws Exception {
        // Should start with no cards
        assertSame(0, this.hand.getSize());
        // Make and add some cards
        final List<WhiteCard> cards = CardHelper.makeWhiteCards("TheHumanity", "squidicuz", "nasonfish");
        this.hand.addCards(cards);
        // Should have the same size
        assertSame(cards.size(), this.hand.getSize());
        // The cards should match
        assertTrue(this.hand.getCards().equals(cards));
    }

    @Test
    public void testClearHand() throws Exception {
        // Should start with no cards
        assertSame(0, this.hand.getSize());
        // Make and add some cards
        final List<WhiteCard> cards = CardHelper.makeWhiteCards("TheHumanity", "squidicuz", "nasonfish");
        this.hand.addCards(cards);
        // Should have the same size
        assertSame(cards.size(), this.hand.getSize());
        // Clear the hand
        this.hand.clearHand();
        // Should be no cards in the hand now
        assertSame(0, this.hand.getSize());
    }

    @Test
    public void testGetCard() throws Exception {
        // Make a card
        final WhiteCard wc = CardHelper.makeWhiteCard("cindy_k");
        // Add the card
        this.hand.addCard(wc);
        // The card at index 0 should be the same as the card just added
        assertEquals(wc, this.hand.getCard(0));
    }

    @Test
    public void testGetCard1() throws Exception {
        // Make a card
        final WhiteCard wc = CardHelper.makeWhiteCard("cindy_k");
        // Add the card
        this.hand.addCard(wc);
        // The card at index 0 (the first card) should be the same as the card just added
        assertEquals(wc, this.hand.getCard("1"));
    }

    @Test
    public void testGetCards() throws Exception {
        // Make some cards
        final List<WhiteCard> cards = CardHelper.makeWhiteCards("lol768", "CardAficionado");
        // Add the cards to the hand
        this.hand.addCards(cards);
        // The hand should have the same list as that which was just added
        assertTrue(cards.equals(this.hand.getCards()));
    }

    @Test
    public void testRemoveCard() throws Exception {
        // There should be no cards in the hand
        assertSame(0, this.hand.getSize());
        // Make and add a new white card
        final List<WhiteCard> wcs = CardHelper.makeWhiteCards("Kashike", "blha303");
        this.hand.addCards(wcs);
        // There should now cards in the hand
        assertSame(wcs.size(), this.hand.getSize());
        // Get one card to remove
        final WhiteCard wc = wcs.get(0);
        // Remove a card and make sure it is successful
        assertTrue(this.hand.removeCard(wc));
        // There should now be one less card in the hand
        assertSame(wcs.size() - 1, this.hand.getSize());
        // Loop through every card and ensure none of them are the removed card
        this.hand.getCards().stream().forEach(card -> assertNotEquals(wc, card));
    }

    @Test
    public void testRemoveCards() throws Exception {
        // There should be no cards in the hand
        assertSame(0, this.hand.getSize());
        // Make some cards and add them
        final List<WhiteCard> cards = CardHelper.makeWhiteCards("A really cool unit test", "jkcclemens", "turt2live");
        this.hand.addCards(cards);
        // There should now be the amount of made cards in the hand
        assertSame(cards.size(), this.hand.getSize());
        // Get a sublist to remove
        final List<WhiteCard> subcards = cards.subList(1, cards.size());
        // Remove the cards
        this.hand.removeCards(subcards);
        // There should now be one card in the hand
        assertSame(1, this.hand.getSize());
        // Loop through every card and ensure none of them are contained in the removed sublist
        this.hand.getCards().forEach(card -> assertFalse(subcards.contains(card)));
    }

    @Test
    public void testToString() throws Exception {
        // Create the necessary include
        final Include include = Include.create().ensure(List.class);
        // The hand should be empty
        assertEquals(
            ReflectiveToStringHelper.of(this.hand, include).generate(),
            this.hand.toString()
        );
        // Make and add a card
        this.hand.addCard(CardHelper.makeWhiteCard("A really cool unit test"));
        // The hand should now reflect this
        assertEquals(
            ReflectiveToStringHelper.of(this.hand, include).generate(),
            this.hand.toString()
        );
    }
}
