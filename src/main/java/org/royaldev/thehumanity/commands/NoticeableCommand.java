package org.royaldev.thehumanity.commands;

import org.pircbotx.User;

public abstract class NoticeableCommand implements IRCCommand {

    public void notice(User u, String message) {
        u.send().notice(message);
    }

}
