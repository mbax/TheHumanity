package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;

public class CardsCommand implements NoticeableCommand {

    final TheHumanity humanity;

    public CardsCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    @Override
    public String getDescription() {
        return "Shows your your cards.";
    }

    @Override
    public String getName() {
        return "cards";
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
            this.notice(u, "You're not in a game.");
            return;
        }
        g.showCards(g.getPlayer(u));
    }
}
