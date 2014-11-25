package org.royaldev.thehumanity.commands;

import org.pircbotx.User;

public interface NoticeableCommand extends IRCCommand {

    default public void notice(final User u, final String message) {
        u.send().notice(message);
    }

}
