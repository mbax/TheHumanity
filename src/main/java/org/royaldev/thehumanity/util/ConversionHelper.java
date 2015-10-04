package org.royaldev.thehumanity.util;

import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.helper.ActorEvent;
import org.kitteh.irc.client.library.event.helper.ChannelEvent;
import org.kitteh.irc.client.library.event.helper.MessageEvent;

public final class ConversionHelper {

    /**
     * Responds to the event with the given message, in the format <code>nickname: message</code>.
     *
     * @param ace     Event to respond to
     * @param message Message to send
     */
    public static void respondChannel(final ChannelEvent ace, final String message) {
        ace.getChannel().sendMessage(((ActorEvent<User>)ace).getActor().getNick() + ": " + message);
    }

    /**
     * Responds to the event with the given message, in the format <code>nickname: message</code>.
     *
     * @param ame     Event to respond to
     * @param message Message to send
     */
    public static void respondActor(final ActorEvent<User> ame, final String message) {
        ame.getActor().sendMessage(message);
    }

    /**
     * Responds to the event with the given message, in the format <code>nickname: message</code>.
     *
     * @param ae      Event to respond to
     * @param message Message to send
     */
    public static void respond(final ActorEvent<User> ae, final String message) {
        if (ae instanceof ChannelEvent) {
            ConversionHelper.respondChannel((ChannelEvent) ae, message);
        } else if (ae instanceof MessageEvent) {
            ConversionHelper.respondActor(ae, message);
        } else {
            throw new IllegalArgumentException("Invalid event type.");
        }
    }

}
