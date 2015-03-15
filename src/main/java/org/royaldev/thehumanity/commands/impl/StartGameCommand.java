package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.cards.cardcast.CardcastFetcher;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.IRCCommand.CommandType;
import org.royaldev.thehumanity.commands.NoticeableCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(
    name = "startgame",
    description = "Starts a new game of Cards Against Humanity.",
    aliases = {"start"},
    usage = "<command> (packs)",
    commandType = CommandType.MESSAGE
)
public class StartGameCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public StartGameCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    /**
     * Adds default packs to the given list of packs if it is empty of if useDefaults is true.
     *
     * @param cardPacks   List of packs to add to
     * @param useDefaults Should defaults be added, even if the list is not empty?
     */
    private void addDefaults(final List<CardPack> cardPacks, final boolean useDefaults) {
        if (cardPacks.isEmpty() || useDefaults) {
            Arrays.stream(this.humanity.getDefaultPacks()).map(this.humanity::getCardPack).filter(cp -> cp != null).forEach(cardPacks::add);
            if (cardPacks.isEmpty()) cardPacks.addAll(this.humanity.getLoadedCardPacks());
        }
    }

    /**
     * Gets the named card packs. If any card pack starts with "cc:", it is assumed to be a Cardcast pack, and an
     * attempt will be made to fetch and convert it for this game. If TheHumanity was started with the option to keep
     * Cardcast packs loaded after downloading, the pack will be added to the main list of global packs.
     * <p/>
     * If any of the listed packs is titled "default", the default packs listed when TheHumanity was started will be
     * added.
     *
     * @param args List of pack names
     * @return List of packs
     */
    private List<CardPack> getCardPacks(final String[] args) {
        final List<CardPack> cardPacks = new ArrayList<>();
        boolean useDefaults = false;
        for (final String cardPack : args) {
            if ("default".equalsIgnoreCase(cardPack)) {
                useDefaults = true;
                continue;
            }
            final CardPack cp;
            if (cardPack.toLowerCase().startsWith("cc:")) {
                cp = this.getCardcastPack(cardPack);
                if (this.humanity.areCardcastPacksKept() && this.humanity.getCardPack(cp.getName()) == null) {
                    this.humanity.addCardPack(cp);
                }
            } else {
                cp = this.humanity.getCardPack(cardPack);
            }
            if (cp == null) continue;
            cardPacks.add(cp);
        }
        this.addDefaults(cardPacks, useDefaults);
        return cardPacks;
    }

    /**
     * Gets a Cardcast pack by its ID.
     *
     * @param name ID
     * @return Pack or null
     */
    private CardPack getCardcastPack(final String name) {
        return new CardcastFetcher(name.substring(3)).getCardPack();
    }

    @Override
    public void onCommand(final GenericMessageEvent event, final CallInfo ci, final String[] args) {
        if (!(event instanceof MessageEvent)) return;
        final User u = event.getUser();
        final MessageEvent e = (MessageEvent) event;
        if (this.humanity.getGames().containsKey(e.getChannel())) {
            this.notice(u, "There is already a game in this channel.");
            return;
        }
        for (final Game game : this.humanity.getGames().values()) {
            if (!game.hasPlayer(e.getUser().getNick())) continue;
            this.notice(u, "You can't be in more than one game at a time!");
            return;
        }
        final List<CardPack> cardPacks = this.getCardPacks(args);
        final Game g = new Game(this.humanity, e.getChannel(), cardPacks);
        this.humanity.getGames().put(e.getChannel(), g);
        g.start();
        g.setHost(g.createPlayer(event.getUser()));
    }
}
