package org.royaldev.thehumanity.server.services.channel;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.Channel;
import org.royaldev.thehumanity.TheHumanity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HumanityChannelService implements ChannelService {

    @Autowired
    private TheHumanity humanity;

    @Override
    public Channel getFromName(@NotNull final String name) {
        return this.humanity.getBot().getChannel(name);
    }
}
