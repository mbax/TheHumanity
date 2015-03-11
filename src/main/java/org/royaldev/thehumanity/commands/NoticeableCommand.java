package org.royaldev.thehumanity.commands;

import org.pircbotx.User;

public interface NoticeableCommand extends IRCCommand {

    /**
     * Sends a notice to the given User.
     *
     * @param u       User to send notice to
     * @param message Message to send
     */
    default public void notice(final User u, final String message) {
        u.send().notice(message);
    }

}
