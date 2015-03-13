package org.royaldev.thehumanity.commands;

import org.pircbotx.User;

public abstract class NoticeableCommand extends IRCCommand {

    /**
     * Sends a notice to the given User.
     *
     * @param u       User to send notice to
     * @param message Message to send
     */
    public void notice(final User u, final String message) {
        u.send().notice(message);
    }

}
