package org.royaldev.thehumanity.cards;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.royaldev.thehumanity.CardHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class CardPackTest {

    private final static String NAME = "Test";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private CardPack cp;

    @Before
    public void setUp() throws Exception {
        this.cp = new CardPack(CardPackTest.NAME);
    }

    @After
    public void tearDown() throws Exception {
        this.cp = null;
    }

    @Test
    public void testAddCard() throws Exception {
        // There should be no white cards
        assertSame(0, this.cp.getWhiteCards().size());
        // There should be no black cards
        assertSame(0, this.cp.getBlackCards().size());
        // Add a white card to the pack
        this.cp.addCard(CardHelper.makeWhiteCard(this.cp, "Dogs"));
        // Add a black card to the pack
        this.cp.addCard(CardHelper.makeBlackCard(this.cp, "What do cats hate? _"));
        // Make sure that there's now one white card
        assertSame(1, this.cp.getWhiteCards().size());
        // Make sure that there's now one black card
        assertSame(1, this.cp.getBlackCards().size());
        // The white card at index 0 should equal the white card we added
        assertEquals(this.cp.getWhiteCards().get(0).getText(), "Dogs");
        // The black card at index 0 should equal the black card we added
        assertEquals(this.cp.getBlackCards().get(0).getText(), "What do cats hate? <BLANK>");
    }

    @Test
    public void testAddCardInvalid() throws Exception {
        // Expect an IllegalArgumentException
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Card did not belong to this CardPack");
        // Try adding a card that doesn't belong to this pack. Should throw an exception
        this.cp.addCard(CardHelper.makeWhiteCard(new CardPack("Not the same pack"), "Some card"));
    }

    @Test
    public void testAuthor() throws Exception {
        // There is no author, so this should return null
        assertNull(this.cp.getAuthor());
        // Set the author
        this.cp.setAuthor("Joe");
        // The author should now be Joe
        assertEquals("Joe", this.cp.getAuthor());
    }

    @Test
    public void testDescription() throws Exception {
        // There is no description, so this should return null
        assertNull(this.cp.getDescription());
        // Set the description
        this.cp.setDescription("My cool pack.");
        // The description should reflect the change
        assertEquals("My cool pack.", this.cp.getDescription());
    }

    @Test
    public void testGetName() throws Exception {
        // The name should be what we initialized it with
        assertEquals(CardPackTest.NAME, this.cp.getName());
    }

    @Test
    public void testGetNameFromFileName() throws Exception {
        // The extension should be removed
        assertEquals(
            "My.Dog",
            CardPack.getNameFromFileName("My.Dog.cards")
        );
        assertEquals(
            "My.Dog",
            CardPack.getNameFromFileName("My.Dog.derpherp")
        );
    }
}
