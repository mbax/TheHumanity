package org.royaldev.thehumanity.util;

import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorChannelEvent;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.kitteh.irc.client.library.event.ActorMessageEvent;

public final class ConversionHelper {

    /**
     * Responds to the event with the given message, in the format <code>nickname: message</code>.
     *
     * @param ace     Event to respond to
     * @param message Message to send
     */
    public static void respond(final ActorChannelEvent<User> ace, final String message) {
        ace.getChannel().sendMessage(ace.getActor().getNick() + ": " + message);
    }

    /**
     * Responds to the event with the given message, in the format <code>nickname: message</code>.
     *
     * @param ame     Event to respond to
     * @param message Message to send
     */
    public static void respond(final ActorMessageEvent<User> ame, final String message) {
        ame.getActor().sendMessage(message);
    }

    /**
     * Responds to the event with the given message, in the format <code>nickname: message</code>.
     *
     * @param ae      Event to respond to
     * @param message Message to send
     */
    public static void respond(final ActorEvent<User> ae, final String message) {
        if (ae instanceof ActorChannelEvent) {
            ConversionHelper.respond((ActorChannelEvent<User>) ae, message);
        } else if (ae instanceof ActorMessageEvent) {
            ConversionHelper.respond((ActorMessageEvent<User>) ae, message);
        } else {
            throw new IllegalArgumentException("Invalid event type.");
        }
    }

}
