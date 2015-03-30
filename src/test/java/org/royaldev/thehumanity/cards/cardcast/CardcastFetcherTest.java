package org.royaldev.thehumanity.cards.cardcast;

import org.junit.Test;
import org.royaldev.thehumanity.CardHelper;
import org.royaldev.thehumanity.cards.Card;
import org.royaldev.thehumanity.cards.packs.CardPack;
import org.royaldev.thehumanity.cards.packs.MemoryCardPack;

import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CardcastFetcherTest {

    private final CardcastFetcher ccf = new CardcastFetcher("XWUGF");

    private CardPack makeTestCardPack() {
        final CardPack cp = new MemoryCardPack("TheHumanity Local Test Pack");
        CardHelper.makeBlackCards(
            cp,
            "_ made the unit test for _.",
            "Why unit test when you have _?",
            "I made a _."
        ).forEach(cp::addCard);
        CardHelper.makeWhiteCards(
            cp,
            "Throwing an AssertionError",
            "JUnit",
            "Making a unit test",
            "Unit-testing",
            "lol768",
            "CardAficionado",
            "TheHumanity",
            "Kashike",
            "blha303",
            "turt2live",
            "jkcclemens",
            "A really cool unit test"
        ).forEach(cp::addCard);
        return cp;
    }

    @Test
    public void testValidityOfConvertedPack() {
        // Get the expected pack
        final CardPack local = this.makeTestCardPack();
        // Get the converted Cardcast pack
        final CardPack converted = this.ccf.getCardPack();
        // Ensure it isn't null
        assertNotNull(converted);
        // Make sure that the name, author, and description match
        assertEquals("TheHumanity Test Deck", converted.getName());
        assertEquals("jkcclemens", converted.getAuthor());
        assertEquals("This deck is made for use with unit tests. It's probably not fun to play with.", converted.getDescription());
        // Make sure that the white cards match
        assertTrue(
            Objects.equals(
                converted.getWhiteCards().stream().map(Card::toString).sorted().collect(Collectors.toList()),
                local.getWhiteCards().stream().map(Card::toString).sorted().collect(Collectors.toList())
            )
        );
        // Make sure that the black cards match
        assertTrue(
            Objects.equals(
                converted.getBlackCards().stream().map(Card::toString).sorted().collect(Collectors.toList()),
                local.getBlackCards().stream().map(Card::toString).sorted().collect(Collectors.toList())
            )
        );
    }
}
