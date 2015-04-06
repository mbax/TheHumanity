package org.royaldev.thehumanity.server.services.game;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.Channel;
import org.royaldev.thehumanity.Game;

public interface GameService {

    Game getFromChannel(@NotNull Channel channel);

    Game getFromChannelName(@NotNull final String channel);

}
