package org.royaldev.thehumanity.server.services.channel;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.Channel;

import java.util.Set;

public interface ChannelService {

    Set<Channel> getAll();

    Channel getFromName(@NotNull final String name);

}
