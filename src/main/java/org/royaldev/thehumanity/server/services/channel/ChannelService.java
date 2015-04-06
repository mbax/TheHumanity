package org.royaldev.thehumanity.server.services.channel;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.Channel;

public interface ChannelService {

    Channel getFromName(@NotNull final String name);

}
