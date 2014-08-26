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
    public String[] getAliases() {
        return new String[]{"start"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MESSAGE;
    }

    @Override
    public String getDescription() {
        return "Starts a new game of Cards Against Humanity.";
    }

    @Override
    public String getName() {
        return "startgame";
    }

    @Override
    public String getUsage() {
        return "<command> (packs)";
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
        if (cardPacks.isEmpty()) {
            for (String cardPack : this.humanity.getDefaultPacks()) {
                final CardPack cp = this.humanity.getCardPack(cardPack);
                if (cp == null) continue;
                cardPacks.add(cp);
            }
            if (cardPacks.isEmpty()) cardPacks.addAll(this.humanity.getLoadedCardPacks());
        }
        final Game g = new Game(this.humanity, e.getChannel(), cardPacks);
        this.humanity.getGames().put(e.getChannel(), g);
        g.start();
        g.setHost(g.createPlayer(event.getUser()));
    }
}
