package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.helper.ActorEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.royaldev.thehumanity.game.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.IRCCommand.CommandType;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.player.Player;

@Command(
    name = "join",
    description = "Joins the current game.",
    aliases = {"joingame"},
    commandType = CommandType.MESSAGE
)
public class JoinGameCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public JoinGameCommand(final TheHumanity humanity) {
        this.humanity = humanity;
    }

    @Override
    public void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        if (!(event instanceof ChannelMessageEvent)) return;
        final ChannelMessageEvent e = (ChannelMessageEvent) event;
        final User u = e.getActor();
        final Game g = this.humanity.getGames().get(e.getChannel());
        if (g == null) {
            this.notice(u, "There's no game right now. Start one with " + IRCFormat.BOLD + this.humanity.getPrefix() + "start" + IRCFormat.RESET + ".");
            return;
        }
        if (g.hasPlayer(u.getNick())) {
            this.notice(u, "You can't join a game you're already in!");
            return;
        }
        for (final Game game : this.humanity.getGames().values()) {
            if (!game.hasPlayer(u.getNick())) continue;
            this.notice(u, "You can't be in more than one game at a time!");
            return;
        }
        final Player p = g.createPlayer(u);
        if (p == null) {
            this.notice(u, "Could not join due to an internal error.");
            return;
        }
        g.addPlayer(p);
    }
}
