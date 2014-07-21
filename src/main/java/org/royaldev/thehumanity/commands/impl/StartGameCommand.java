package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;

import java.util.ArrayList;
import java.util.List;

public class StartGameCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public StartGameCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        if (!(event instanceof MessageEvent)) return;
        final User u = event.getUser();
        final MessageEvent e = (MessageEvent) event;
        if (this.humanity.getGames().containsKey(e.getChannel())) {
            this.notice(u, "There is already a game in this channel.");
            return;
        }
        final List<CardPack> cardPacks = new ArrayList<>();
        for (final String cardPack : args) {
            final CardPack cp = this.humanity.getCardPack(cardPack);
            if (cp == null) continue;
            cardPacks.add(cp);
        }
        if (cardPacks.isEmpty()) cardPacks.addAll(this.humanity.getLoadedCardPacks());
        final Game g = new Game(this.humanity, e.getChannel(), cardPacks);
        g.setHost(event.getUser());
        this.humanity.getGames().put(e.getChannel(), g);
        g.start();
    }

    @Override
    public String getName() {
        return "startgame (packs)";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public String getDescription() {
        return "Starts a new game of Cards Against Humanity.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"start"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MESSAGE;
    }
}
