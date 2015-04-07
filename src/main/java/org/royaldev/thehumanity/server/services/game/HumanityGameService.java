package org.royaldev.thehumanity.server.services.game;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.Channel;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.server.services.channel.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HumanityGameService implements GameService {

    @Autowired
    private TheHumanity humanity;
    @Autowired
    private ChannelService channelService;

    @Override
    public Game getFromChannel(@NotNull final Channel channel) {
        return this.humanity.getGameFor(channel);
    }

    @Override
    public Game getFromChannelName(@NotNull final String channel) {
        return this.getFromChannel(this.channelService.getFromName(channel));
    }
}