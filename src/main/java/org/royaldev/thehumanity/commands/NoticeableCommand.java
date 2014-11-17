package org.royaldev.thehumanity.commands;

import org.pircbotx.User;

public abstract class NoticeableCommand implements IRCCommand {

    public void notice(final User u, final String message) {
        u.send().notice(message);
    }

}
