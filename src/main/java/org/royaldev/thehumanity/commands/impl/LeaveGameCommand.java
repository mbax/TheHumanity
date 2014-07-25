package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.player.Player;

public class LeaveGameCommand extends NoticeableCommand {

    private TheHumanity humanity;

    public LeaveGameCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"leavegame", "part", "partgame"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    @Override
    public String getDescription() {
        return "Leaves the game you're in.";
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        final User u = event.getUser();
        final Game g = this.humanity.getGameFor(u);
        if (g == null) {
            this.notice(u, "You're not in any game!");
            return;
        }
        final Player p = g.getPlayer(u);
        g.removePlayer(p);
    }
}
