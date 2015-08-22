package org.royaldev.thehumanity.cards.types;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kitteh.irc.client.library.IRCFormat;
import org.royaldev.thehumanity.CardHelper;
import org.royaldev.thehumanity.cards.packs.MemoryCardPack;
import org.royaldev.thehumanity.cards.play.Play;
import org.royaldev.thehumanity.player.Player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.powermock.api.mockito.PowerMockito.mock;

public class BlackCardTest {

    private BlackCard oneBlank, twoBlanks;

    @Before
    public void setUp() throws Exception {
        this.oneBlank = new BlackCard(mock(MemoryCardPack.class), "Who is the guy? _");
        this.twoBlanks = new BlackCard(mock(MemoryCardPack.class), "Who are the guys? _ _");
    }

    @After
    public void tearDown() throws Exception {
        this.oneBlank = this.twoBlanks = null;
    }

    @Test
    public void testFillInBlanks() throws Exception {
        assertEquals(
            "Who is the guy? " + IRCFormat.BOLD + "Dave" + IRCFormat.RESET,
            this.oneBlank.fillInBlanks(new Play(mock(Player.class), CardHelper.makeWhiteCards("Dave"), new int[]{1}))
        );
        assertEquals(
            "Who are the guys? " + IRCFormat.BOLD + "Dave" + IRCFormat.RESET + " " + IRCFormat.BOLD + "Mike" + IRCFormat.RESET,
            this.twoBlanks.fillInBlanks(new Play(mock(Player.class), CardHelper.makeWhiteCards("Dave", "Mike"), new int[]{1, 2}))
        );
    }

    @Test
    public void testGetBlanks() throws Exception {
        assertSame(1, this.oneBlank.getBlanks());
        assertSame(2, this.twoBlanks.getBlanks());
    }
}
