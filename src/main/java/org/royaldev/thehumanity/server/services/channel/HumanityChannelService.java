package org.royaldev.thehumanity.server.services.channel;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.Channel;
import org.royaldev.thehumanity.TheHumanity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class HumanityChannelService implements ChannelService {

    @Autowired
    private TheHumanity humanity;

    @Override
    public Set<Channel> getAll() {
        return this.humanity.getBot().getChannels();
    }

    @Override
    public Channel getFromName(@NotNull final String name) {
        Preconditions.checkNotNull(name, "name was null");
        return this.humanity.getBot().getChannel(name);
    }
}
