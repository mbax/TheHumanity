package org.royaldev.thehumanity.util;

import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorChannelEvent;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.kitteh.irc.client.library.event.ActorMessageEvent;

public final class ConversionHelper {

    public static void respond(final ActorChannelEvent<User> ace, final String message) {
        ace.getChannel().sendMessage(ace.getActor().getNick() + ": " + message);
    }

    public static void respond(final ActorMessageEvent<User> ame, final String message) {
        ame.getActor().sendMessage(message);
    }

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
