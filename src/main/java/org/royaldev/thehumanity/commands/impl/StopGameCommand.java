package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.player.Player;

public class StopGameCommand implements NoticeableCommand {

    private final TheHumanity humanity;

    public StopGameCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"stop"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MESSAGE;
    }

    @Override
    public String getDescription() {
        return "Stops the game you're in, if you're an operator in its channel.";
    }

    @Override
    public String getName() {
        return "stopgame";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        if (!(event instanceof MessageEvent)) return;
        final User u = event.getUser();
        final MessageEvent e = (MessageEvent) event;
        final Game g = this.humanity.getGameFor(e.getChannel());
        if (g == null) {
            this.notice(u, "No game in this channel!");
            return;
        }
        final Player p = g.getPlayer(u);
        if (!g.getChannel().getOps().contains(u) && !g.getHost().equals(p)) {
            this.notice(u, "You're not an op or the host!");
            return;
        }
        g.stop();
    }
}
