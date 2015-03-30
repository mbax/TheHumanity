package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.cardcast.CardcastFetcher;
import org.royaldev.thehumanity.cards.packs.CardPack;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.IRCCommand.CommandType;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.player.Player;

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
     * Gets a Cardcast pack by its ID.
     *
     * @param name ID
     * @return Pack or null
     */
    private CardPack getCardcastPack(final String name) {
        return new CardcastFetcher(name.substring(3)).getCardPack();
    }

    /**
     * Tries to skip the countdown and rush the Game to start.
     *
     * @param e Event
     * @param u User trying to rush
     */
    private void rush(final ChannelMessageEvent e, final User u) {
        final Game g = this.humanity.getGameFor(e.getChannel());
        if (g == null) {
            this.notice(u, "There is no game in this channel.");
            return;
        }
        final Player p = g.getPlayer(u);
        if (p == null || !p.equals(g.getHost()) && !this.humanity.hasChannelMode(g.getChannel(), p.getUser(), 'o')) {
            this.notice(u, "There is already a game in this channel.");
            return;
        }
        this.notice(u, "The countdown was" + (g.skipCountdown() ? "" : " not") + " skipped.");
    }

    @Override
    public void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        if (!(event instanceof ChannelMessageEvent)) return;
        final User u = event.getActor();
        final ChannelMessageEvent e = (ChannelMessageEvent) event;
        if (this.humanity.getGames().containsKey(e.getChannel())) {
            this.rush(e, u);
            return;
        }
        for (final Game game : this.humanity.getGames().values()) {
            if (!game.hasPlayer(u.getNick())) continue;
            this.notice(u, "You can't be in more than one game at a time!");
            return;
        }
        final List<CardPack> cardPacks = this.humanity.getCardPacksFromArguments(args);
        final Game g = new Game(this.humanity, e.getChannel(), cardPacks);
        this.humanity.getGames().put(e.getChannel(), g);
        g.start();
        final Player p = g.createPlayer(u);
        if (p == null) {
            this.notice(u, "You could not be set as host due to an internal error.");
            return;
        }
        g.setHost(p);
    }
}
