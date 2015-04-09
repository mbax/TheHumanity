package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.IRCCommand.CommandType;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.game.Game;
import org.royaldev.thehumanity.game.Game.GameEndCause;
import org.royaldev.thehumanity.player.Player;

@Command(
    name = "stopgame",
    description = "Stops the game you're in, if you're an operator or the host in its channel.",
    aliases = {"stop"},
    commandType = CommandType.MESSAGE
)
public class StopGameCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public StopGameCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        if (!(event instanceof ChannelMessageEvent)) return;
        final User u = event.getActor();
        final ChannelMessageEvent e = (ChannelMessageEvent) event;
        final Game g = this.humanity.getGameFor(e.getChannel());
        if (g == null) {
            this.notice(u, "No game in this channel!");
            return;
        }
        final Player p = g.getPlayer(u);
        if (!g.getHost().equals(p) && !this.humanity.hasChannelMode(g.getChannel(), u, 'o')) {
            this.notice(u, "You're not an op or the host!");
            return;
        }
        g.stop(GameEndCause.STOPPED_BY_COMMAND);
    }
}
