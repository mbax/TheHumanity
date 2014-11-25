package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;

public class JoinGameCommand implements NoticeableCommand {

    private final TheHumanity humanity;

    public JoinGameCommand(final TheHumanity humanity) {
        this.humanity = humanity;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"joingame"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MESSAGE;
    }

    @Override
    public String getDescription() {
        return "Joins the current game.";
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        if (!(event instanceof MessageEvent)) return;
        final MessageEvent e = (MessageEvent) event;
        final User u = e.getUser();
        final Game g = this.humanity.getGames().get(e.getChannel());
        if (g == null) {
            this.notice(u, "There's no game right now. Start one with " + Colors.BOLD + this.humanity.getPrefix() + "start" + Colors.NORMAL + ".");
            return;
        }
        if (g.hasPlayer(u.getNick())) {
            this.notice(u, "You can't join a game you're already in!");
            return;
        }
        for (final Game game : this.humanity.getGames().values()) {
            if (!game.hasPlayer(e.getUser().getNick())) continue;
            this.notice(u, "You can't be in more than one game at a time!");
            return;
        }
        g.addPlayer(g.createPlayer(u));
    }
}
