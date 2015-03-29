package org.royaldev.thehumanity.commands;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kitteh.irc.client.library.element.User;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;

public class NoticeableCommandTest {

    private User user;
    private NoticeableCommand command;

    private NoticeableCommand makeCommand() {
        final NoticeableCommand nc = mock(NoticeableCommand.class);
        doCallRealMethod().when(nc).notice(any(User.class), anyString());
        return nc;
    }

    private User makeUser() {
        final User u = mock(User.class);
        doNothing().when(u).sendNotice(anyString());
        return u;
    }

    @Before
    public void setUp() throws Exception {
        this.command = this.makeCommand();
        this.user = this.makeUser();
    }

    @After
    public void tearDown() throws Exception {
        this.command = null;
        this.user = null;
    }

    @Test
    public void testNotice() throws Exception {
        final String message = "Hello!";
        // Send the message to the user
        this.command.notice(this.user, message);
        // Verify that sendNotice was called on the user with the message
        verify(this.user).sendNotice(eq(message));
    }
}
