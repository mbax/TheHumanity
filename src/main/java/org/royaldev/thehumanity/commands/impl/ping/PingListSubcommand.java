package org.royaldev.thehumanity.commands.impl.ping;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;

public abstract class PingListSubcommand extends NoticeableCommand {

    public abstract void onSubcommand(@NotNull final ChannelMessageEvent event, @NotNull final CallInfo ci, @NotNull final String[] args);

    @Override
    public void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        final User u = event.getActor();
        if (!(event instanceof ChannelMessageEvent)) {
            this.notice(u, "This command must be used in a channel.");
            return;
        }
        this.onSubcommand((ChannelMessageEvent) event, ci, args);
    }
}
