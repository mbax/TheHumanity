package org.royaldev.thehumanity.cards;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.royaldev.thehumanity.CardHelper;

import java.util.Arrays;

@Ignore
public class DeckTest {

    private Deck deck;

    @Before
    public void setUp() throws Exception {
        this.deck = CardHelper.makeDeck(
            Arrays.asList(
                CardHelper.makeCardPack(
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
                ),
                CardHelper.makeCardPack(
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
                )
            )
        );
    }

    @After
    public void tearDown() throws Exception {
        this.deck = null;
    }

    @Test
    public void testAddCardPack() throws Exception {

    }

    @Test
    public void testGetBlackCardCount() throws Exception {

    }

    @Test
    public void testGetCardPacks() throws Exception {

    }

    @Test
    public void testGetRandomBlackCard() throws Exception {

    }

    @Test
    public void testGetRandomWhiteCard() throws Exception {

    }

    @Test
    public void testGetUnusedBlackCardCount() throws Exception {

    }

    @Test
    public void testGetUnusedWhiteCardCount() throws Exception {

    }

    @Test
    public void testGetWhiteCardCount() throws Exception {

    }

    @Test
    public void testRemoveCardPack() throws Exception {

    }

    @Test
    public void testRepopulateBlackCards() throws Exception {

    }

    @Test
    public void testRepopulateWhiteCards() throws Exception {

    }

    @Test
    public void testRepopulateWhiteCards1() throws Exception {

    }
}
