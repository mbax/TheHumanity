package org.royaldev.thehumanity.player;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kitteh.irc.client.library.element.User;
import org.royaldev.thehumanity.CardHelper;
import org.royaldev.thehumanity.cards.types.BlackCard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class PlayerTest {

    private static final String NICK = "mbaxter";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Player player;

    private User makeUser(final String nick) {
        final User u = mock(User.class);
        when(u.getNick()).thenReturn(nick);
        return u;
    }

    @Before
    public void setUp() throws Exception {
        this.player = new Player(this.makeUser(PlayerTest.NICK));
    }

    @After
    public void tearDown() throws Exception {
        this.player = null;
    }

    @Test
    public void testAddWin() throws Exception {
        // Player should have no wins
        assertSame(0, this.player.getWins().size());
        // Make a new winning card
        final BlackCard bc = CardHelper.makeBlackCard("_ is great.");
        // Add the win
        this.player.addWin(bc);
        // There should be one win now
        assertSame(1, this.player.getWins().size());
        // The win should be the same as the card added
        assertEquals(bc.getText(), this.player.getWins().iterator().next().getText());
    }

    @Test
    public void testClearWins() throws Exception {
        // Start with no wins
        assertSame(0, this.player.getWins().size());
        // Add a win
        this.player.addWin(CardHelper.makeBlackCard("What am I testing? _"));
        // Have one win now
        assertSame(1, this.player.getWins().size());
        // Clear the wins
        this.player.clearWins();
        // Should have no wins now
        assertSame(0, this.player.getWins().size());
    }

    @Test
    public void testEquals() throws Exception {
        // Player checks if the nicks are the same, without checking case. This should always be true.
        assertEquals(this.player, new Player(this.makeUser(PlayerTest.NICK.toLowerCase())));
    }

    @Test
    public void testGetHand() throws Exception {
        // This should never be null
        assertNotNull(this.player.getHand());
    }

    @Test
    public void testGetScore() throws Exception {
        // The score should be the size of the player's wins
        assertSame(this.player.getWins().size(), this.player.getScore());
        // Add a win
        this.player.addWin(CardHelper.makeBlackCard("What does mbaxter prefer? _"));
        // Should reflect the change
        assertSame(this.player.getWins().size(), this.player.getScore());
    }

    @Test
    public void testGetUser() throws Exception {
        // The user should be the same one we made
        assertEquals(PlayerTest.NICK, this.player.getUser().getNick());
    }

    @Test
    public void testGetWins() throws Exception {
        // Expect an UnsupportedOperationException
        this.thrown.expect(UnsupportedOperationException.class);
        // Modifying the list in any way should throw an exception
        this.player.getWins().add(CardHelper.makeBlackCard("What made me win? _"));
    }

    @Test
    public void testRemoveWin() throws Exception {
        // Player should have no wins
        assertSame(0, this.player.getWins().size());
        // Make a new winning cards
        final BlackCard firstWin = CardHelper.makeBlackCard("_ is great.");
        final BlackCard secondWin = CardHelper.makeBlackCard("What's mbaxter's pastime? _");
        // Add the wins
        this.player.addWin(firstWin);
        this.player.addWin(secondWin);
        // There should be two wins now
        assertSame(2, this.player.getWins().size());
        // Remove one win
        this.player.removeWin(secondWin);
        // There should be one win now
        assertSame(1, this.player.getWins().size());
        // The win left over should match the one not removed
        assertEquals(firstWin.getText(), this.player.getWins().iterator().next().getText());
    }

    @Test
    public void testSetUser() throws Exception {
        final String otherNick = "JoeSchmoe";
        // Shouldn't start with this nick, or the test won't work
        assertFalse(this.player.getUser().getNick().equals(otherNick));
        // Set the user to a user with the other nick
        this.player.setUser(this.makeUser(otherNick));
        // The user's nick should now match
        assertEquals(otherNick, this.player.getUser().getNick());
    }
}
