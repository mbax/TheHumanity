package org.royaldev.thehumanity.commands;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;

/**
 * A normal {@link IRCCommand} with a {@link #notice} method.
 */
public abstract class NoticeableCommand extends IRCCommand {

    /**
     * Sends a notice to the given User.
     *
     * @param u       User to send notice to
     * @param message Message to send
     */
    public void notice(@NotNull final User u, @NotNull final String message) {
        Validate.notNull(u, "u was null");
        Validate.notNull(message, "message was null");
        u.sendNotice(message);
    }

}
