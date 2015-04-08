package org.royaldev.thehumanity.server.services.game;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.Channel;
import org.royaldev.thehumanity.Game;

import java.util.Map;

public interface GameService {

    Map<Channel, Game> getAll();

    Game getFromChannel(@NotNull Channel channel);

    Game getFromChannelName(@NotNull final String channel);

}
