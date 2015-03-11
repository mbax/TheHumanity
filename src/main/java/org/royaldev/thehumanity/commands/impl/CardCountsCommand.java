package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;

public class CardCountsCommand implements NoticeableCommand {

    private final TheHumanity humanity;

    public CardCountsCommand(final TheHumanity humanity) {
        this.humanity = humanity;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cardcount", "cc"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    @Override
    public String getDescription() {
        return "Displays the current amount of cards in the deck.";
    }

    @Override
    public String getName() {
        return "cardcounts";
    }

    @Override
    public String getUsage() {
        return "<command> (public)";
    }

    @Override
    public void onCommand(final GenericMessageEvent event, final CallInfo ci, final String[] args) {
        final User u = event.getUser();
        final Game g = this.humanity.getGameFor(u);
        if (g == null) {
            this.notice(u, "You're not in a game.");
            return;
        }
        if (args.length > 0 && "public".equalsIgnoreCase(args[0])) {
            g.showCardCounts();
        } else {
            this.notice(u, g.getCardCounts());
        }
    }
}
