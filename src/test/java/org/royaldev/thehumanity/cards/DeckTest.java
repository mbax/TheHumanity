package org.royaldev.thehumanity.cards;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.royaldev.thehumanity.CardHelper;
import org.royaldev.thehumanity.cards.packs.CardPack;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.player.Hand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DeckTest {

    private Deck deck;
    private CardPack packOne, packTwo;

    @Before
    public void setUp() throws Exception {
        this.packOne = CardHelper.makeCardPack(
            "Pack 1",
            Arrays.asList(
                "The first pack",
                "Eating the first pack",
                "Loving the first pack"
            ),
            Arrays.asList(
                "What is great? _",
                "Who is great? _"
            )
        );
        this.packTwo = CardHelper.makeCardPack(
            "Pack 2",
            Arrays.asList(
                "The second pack",
                "Listening to the second pack",
                "Caring for the second pack"
            ),
            Arrays.asList(
                "My favorite thing is _.",
                "What is _?"
            )
        );
        this.deck = CardHelper.makeDeck(Arrays.asList(packOne, packTwo));
    }

    @After
    public void tearDown() throws Exception {
        this.deck = null;
    }

    @Test
    public void testAddCardPack() throws Exception {
        // We start with two packs
        assertSame(2, this.deck.getCardPacks().size());
        // Make a new test pack
        final CardPack test = CardHelper.makeCardPack("Test", Collections.singletonList("Some guy"), Collections.singletonList("Who are you? _"));
        // Add the pack
        this.deck.addCardPack(test);
        // Should now have three packs in the deck
        assertSame(3, this.deck.getCardPacks().size());
        // The third pack should be the one we inserted
        assertEquals(test, this.deck.getCardPacks().get(2));
    }

    @Test
    public void testGetBlackCardCount() throws Exception {
        // Should be four cards
        assertSame(4, this.deck.getBlackCardCount());
    }

    @Test
    public void testGetCardPacks() throws Exception {
        // First pack should be the first one added
        assertEquals(this.packOne, this.deck.getCardPacks().get(0));
        // Second pack should be the second one added
        assertEquals(this.packTwo, this.deck.getCardPacks().get(1));
    }

    @Test
    public void testGetRandomBlackCard() throws Exception {
        // Get the amount of black cards
        final int previousAmount = this.deck.getBlackCardCount();
        // Get a random card
        this.deck.getRandomBlackCard();
        // Should have one less black card
        assertSame(previousAmount - 1, this.deck.getUnusedBlackCardCount());
        // The packs should remain untouched
        assertSame(previousAmount, this.deck.getCardPacks().stream().mapToInt(pack -> pack.getBlackCards().size()).sum());
    }

    @Test
    public void testGetRandomWhiteCard() throws Exception {
        // Get the amount of white cards
        final int previousAmount = this.deck.getWhiteCardCount();
        // Get a random card, no exclusions
        this.deck.getRandomWhiteCard(null);
        // Should have one less white card
        assertSame(previousAmount - 1, this.deck.getUnusedWhiteCardCount());
        // The packs should remain untouched
        assertSame(previousAmount, this.deck.getCardPacks().stream().mapToInt(pack -> pack.getWhiteCards().size()).sum());
    }

    @Test
    public void testGetWhiteCardCount() throws Exception {
        // Should be six cards
        assertSame(6, this.deck.getWhiteCardCount());
    }

    @Test
    public void testRemoveCardPack() throws Exception {
        // Start with two packs
        assertSame(2, this.deck.getCardPacks().size());
        // Remove the first pack
        this.deck.removeCardPack(this.packOne);
        // Should only have one pack now
        assertSame(1, this.deck.getCardPacks().size());
        // The pack that is left should be the second pack
        assertEquals(this.packTwo, this.deck.getCardPacks().get(0));
    }

    @Test
    public void testRepopulateBlackCards() throws Exception {
        // Get the original amount of unused black cards
        final int amount = this.deck.getUnusedBlackCardCount();
        // Draw random black cards until none are left
        BlackCard random;
        do {
            random = this.deck.getRandomBlackCard();
        } while (random != null);
        // There should be no cards left
        assertSame(0, this.deck.getUnusedBlackCardCount());
        // Repopulate the cards
        this.deck.repopulateBlackCards();
        // Should be back to how many we had at the start
        assertSame(amount, this.deck.getUnusedBlackCardCount());
    }

    @Test
    public void testRepopulateWhiteCards() throws Exception {
        // Get the original amount of unused white cards
        final int amount = this.deck.getUnusedWhiteCardCount();
        // Clear out the white cards
        while (this.deck.getUnusedWhiteCardCount() > 0) {
            this.deck.getRandomWhiteCard(null); // TODO: Test this with non-null
        }
        // There should be no white cards left
        assertSame(0, this.deck.getUnusedWhiteCardCount());
        // Repopulate
        this.deck.repopulateWhiteCards();
        // Should have the same amount as we started with
        assertSame(amount, this.deck.getUnusedWhiteCardCount());
    }

    @Test
    public void testRepopulateWhiteCards1() throws Exception {
        // Clear out the white cards
        while (this.deck.getUnusedWhiteCardCount() > 0) {
            this.deck.getRandomWhiteCard(null);
        }
        // There should be no white cards left
        assertSame(0, this.deck.getUnusedWhiteCardCount());
        // Get all the white cards
        final List<WhiteCard> allWhiteCards = Lists.newArrayList();
        this.deck.getCardPacks().stream()
            .map(CardPack::getWhiteCards)
            .forEach(allWhiteCards::addAll);
        // Make sure there ARE white cards, and enough to effectively test
        assertTrue(allWhiteCards.size() > 1);
        // Remove one white card and keep it
        final WhiteCard keep = allWhiteCards.remove(0);
        // Make a new hand
        final Hand<WhiteCard> hand = new Hand<>();
        // Add all of the other cards to this hand
        hand.addCards(allWhiteCards);
        // Repopulate, excluding all but one card
        this.deck.repopulateWhiteCards(Collections.singletonList(hand));
        // Should only have one card now
        assertSame(1, this.deck.getUnusedWhiteCardCount());
        // Should be the same card that we kept before
        assertEquals(keep, this.deck.getRandomWhiteCard(null));
    }
}
