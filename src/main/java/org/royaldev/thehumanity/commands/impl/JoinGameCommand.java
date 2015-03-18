package org.royaldev.thehumanity.commands.impl;

import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.IRCCommand.CommandType;
import org.royaldev.thehumanity.commands.NoticeableCommand;

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
    public void onCommand(final ActorEvent<User> event, final CallInfo ci, final String[] args) {
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
        g.addPlayer(g.createPlayer(u));
    }
}
