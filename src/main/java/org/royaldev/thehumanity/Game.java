package org.royaldev.thehumanity;

import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.royaldev.thehumanity.Round.RoundStage;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.cards.Deck;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.player.Hand;
import org.royaldev.thehumanity.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

// TODO: Skip timeout

public class Game {

    private final TheHumanity humanity;
    /**
     * A list of the current players in this game.
     */
    private final List<Player> players = Collections.synchronizedList(new ArrayList<>());
    /**
     * A list of all the players that have ever played in this game.
     */
    private final List<Player> allPlayers = Collections.synchronizedList(new ArrayList<>());
    private final Deck deck;
    private Channel channel;
    private Round currentRound = null;
    private Player host = null;
    private ScheduledFuture countdownTask;
    private GameStatus gameStatus = GameStatus.IDLE;
    private boolean hostWasVoiced = false;

    public Game(final TheHumanity humanity, final Channel channel, final List<CardPack> cardPacks) {
        this.humanity = humanity;
        this.channel = channel;
        this.deck = new Deck(cardPacks);
    }

    /**
     * Adds a CardPack to this Game.
     *
     * @param cp CardPack to add.
     * @return true if pack was added, false if otherwise
     */
    public boolean addCardPack(final CardPack cp) {
        return this.deck.addCardPack(cp);
    }

    /**
     * Adds a player to the game. If there are not enough white cards to deal this player in, the game will end. If this
     * Player has previously played, all cards and points will be restored to it.
     *
     * @param player Player to add to the game
     */
    public void addPlayer(final Player player) {
        if (this.hasPlayer(player)) return;
        if (!this.setOldUserData(player)) {
            synchronized (this.players) {
                this.players.add(player);
            }
            synchronized (this.allPlayers) {
                this.allPlayers.add(player);
            }
        }
        this.update();
        final int totalCards = this.getDeck().getCardPacks().stream().mapToInt(cp -> cp.getWhiteCards().size()).sum();
        if (this.players.size() * 7 >= totalCards) {
            this.sendMessage(IRCFormat.BOLD + "Not enough white cards to play!");
            this.stop();
            return;
        }
        this.deal(player);
        if (this.gameStatus != GameStatus.JOINING) this.showCards(player);
        this.sendMessage(IRCFormat.BOLD + player.getUser().getNick() + IRCFormat.RESET + " has joined the game!");
    }

    /**
     * Advances the game stage. This should be called after every Round terminates.
     */
    public void advanceStage() {
        switch (this.gameStatus) {
            case IDLE:
                this.gameStatus = GameStatus.JOINING;
                break;
            case JOINING:
                this.gameStatus = GameStatus.PLAYING;
                break;
        }
        this.processStatus(this.gameStatus);
    }

    /**
     * Reformats a message to ensure no User in IRC is pinged by this message.
     *
     * @param message Message to reformat
     * @return Reformatted message
     */
    public String antiPing(String message) {
        for (final User u : this.channel.getUsers().keySet()) {
            if (u.getNick().length() <= 1) continue;
            message = message.replace(u.getNick(), u.getNick().substring(0, 1) + "\u200b" + u.getNick().substring(1));
        }
        return message;
    }

    /**
     * Creates a Player from a User.
     *
     * @param u User to create Player from
     * @return Player
     */
    public Player createPlayer(final User u) {
        if (this.hasPlayer(u.getNick())) return this.getPlayer(u.getNick());
        final Player p = new Player(u);
        this.addPlayer(p);
        return p;
    }

    /**
     * Deals cards to the given Player until it has ten in its hand.
     *
     * @param player Player to deal to
     */
    public void deal(final Player player) {
        final Hand<WhiteCard> hand = player.getHand();
        while (hand.getSize() < 10) hand.addCard(this.getDeck().getRandomWhiteCard(null));
    }

    /**
     * Deals to each Player in the game until they all have ten cards.
     */
    public void deal() {
        synchronized (this.players) {
            this.players.stream().forEach(this::deal);
        }
    }

    /**
     * Creates a String with the current card counts.
     *
     * @return String
     */
    public String getCardCounts() {
        return IRCFormat.BOLD + "Card counts: "
            + IRCFormat.RESET + this.getDeck().getUnusedBlackCardCount() + " unused/" + this.getDeck().getBlackCardCount() + " black cards, "
            + this.getDeck().getUnusedWhiteCardCount() + " unused/" + this.getDeck().getWhiteCardCount() + " white cards";
    }

    /**
     * Gets the Channel that this game is taking place in.
     *
     * @return Channel
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * Sets the Channel that this Game is in.
     *
     * @param channel Channel
     */
    private void setChannel(final Channel channel) {
        this.channel = channel;
    }

    /**
     * Gets the current Round.
     *
     * @return Round
     */
    public Round getCurrentRound() {
        return this.currentRound;
    }

    /**
     * Gets the Deck being used for this game.
     *
     * @return Deck
     */
    public Deck getDeck() {
        return this.deck;
    }

    /**
     * Gets the current status of this game.
     *
     * @return GameStatus
     */
    public GameStatus getGameStatus() {
        return this.gameStatus;
    }

    /**
     * Sets the current status of this game.
     *
     * @param s Status to set
     */
    public void setGameStatus(final GameStatus s) {
        this.gameStatus = s;
    }

    /**
     * Returns the internal list of all players that have ever played in this game.
     *
     * @return List
     */
    public List<Player> getHistoricPlayers() {
        return this.allPlayers;
    }

    /**
     * Gets the host of this game.
     *
     * @return Player
     */
    public Player getHost() {
        return this.host;
    }

    /**
     * Sets the host of this game. This player will be given voice.
     *
     * @param host Player to set as host
     */
    public void setHost(final Player host) {
        this.host = host;
        this.hostWasVoiced = this.humanity.hasChannelMode(this.channel, this.getHost().getUser(), 'v');
        if (!this.hostWasVoiced) {
            this.humanity.getBot().sendRawLine("MODE " + this.channel.getName() + " +v " + this.host.getUser().getNick());
        }
        this.showHost();
    }

    /**
     * Gets the instance of the bot that this game is running under.
     *
     * @return TheHumanity
     */
    public TheHumanity getHumanity() {
        return this.humanity;
    }

    /**
     * Gets a Player for the given User. If there is no corresponding Player, null is returned.
     *
     * @param u User to get Player for
     * @return Corresponding Player
     */
    public Player getPlayer(final User u) {
        if (u == null) return null;
        return this.getPlayers().stream().filter(p -> this.humanity.usersMatch(u, p.getUser())).findFirst().orElse(null);
    }

    /**
     * Gets a player in this game from the list of players.
     *
     * @param p Player to get
     * @return Player or null
     */
    public Player getPlayer(final Player p) {
        if (p == null) return null;
        return this.getPlayers().get(this.getPlayers().indexOf(p));
    }

    /**
     * Gets a Player in this game based on its name.
     *
     * @param name Name of the Player to retrieve
     * @return Player or null
     */
    public Player getPlayer(final String name) {
        if (name == null) return null;
        return this.getPlayer(this.channel.getUsers().keySet().stream().filter(u -> u.getNick().equalsIgnoreCase(name)).findFirst().orElse(null));
    }

    /**
     * Gets all players in this game.
     *
     * @return List&lt;Player&gt;
     */
    public List<Player> getPlayers() {
        synchronized (this.players) {
            return new ArrayList<>(this.players);
        }
    }

    /**
     * Checks to see if the game has enough players to continue. If it does not, the game will end.
     *
     * @return true if enough players, false if otherwise
     */
    public boolean hasEnoughPlayers() {
        if (this.getPlayers().size() < 3) {
            this.sendMessage(IRCFormat.BOLD + "Not enough players to continue!");
            this.stop();
            return false;
        }
        return true;
    }

    /**
     * Checks to see if the given player name is in this game.
     *
     * @param name Name to check
     * @return true if name is in the game, false if otherwise
     */
    public boolean hasPlayer(final String name) {
        return this.getPlayer(name) != null;
    }

    /**
     * Checks to see if the given Player is in this game.
     *
     * @param p Player to check
     * @return true if player is in the game, false if otherwise
     */
    public boolean hasPlayer(final Player p) {
        return this.players.contains(p);
    }

    /**
     * Devoices the current host and sets a new host (voicing him).
     */
    public void nextHost() {
        if (this.host != null && !this.hostWasVoiced) {
            this.humanity.getBot().sendRawLine("MODE " + this.channel.getName() + " -v " + this.host.getUser().getNick());
            this.host = null;
        }
        synchronized (this.players) {
            if (this.players.size() < 1) return; // should never happen without the game stopping
            this.setHost(this.players.get(0));
        }
    }

    /**
     * Processes steps to take with the given GameStatus.
     *
     * @param newStatus New status of the game.
     */
    public void processStatus(final GameStatus newStatus) {
        if (newStatus == GameStatus.IDLE) return;
        else if (newStatus == GameStatus.JOINING) {
            this.countdownTask = this.humanity.getThreadPool().scheduleAtFixedRate(new GameCountdown(), 0L, 15L, TimeUnit.SECONDS);
        }
        switch (newStatus) {
            case JOINING:
                final StringBuilder sb = new StringBuilder();
                sb.append(IRCFormat.BOLD).append("Card packs for this game:").append(IRCFormat.RESET).append(" ");
                this.getDeck().getCardPacks().stream().forEach(cp -> sb.append(cp.getName()).append(", "));
                this.sendMessage(IRCFormat.BOLD + "A new game is starting!");
                this.sendMessage(sb.toString().substring(0, sb.length() - 2));
                this.showCardCounts();
                this.sendMessage("Use " + IRCFormat.BOLD + this.humanity.getPrefix() + "join" + IRCFormat.RESET + " to join.");
                break;
            case PLAYING:
                if (!this.hasEnoughPlayers()) return;
                if (this.getCurrentRound() != null) {
                    this.showScores();
                    this.showCardCounts();
                }
                int index = this.getCurrentRound() == null ? 0 : this.getPlayers().indexOf(this.getCurrentRound().getCzar()) + 1;
                if (index >= this.getPlayers().size()) index = 0;
                BlackCard blackCard = null;
                do {
                    if (blackCard != null) {
                        this.sendMessage("Black card " + IRCFormat.BOLD + blackCard.getText() + IRCFormat.RESET + " was skipped because it is invalid.");
                    }
                    blackCard = this.getDeck().getRandomBlackCard();
                    if (blackCard == null) {
                        this.sendMessage(" ");
                        this.sendMessage(IRCFormat.BOLD + "There are no more black cards!");
                        this.stop();
                        return;
                    }
                } while (blackCard.getBlanks() > 10 || blackCard.getBlanks() < 1);
                this.currentRound = new Round(this, this.getCurrentRound() == null ? 1 : this.getCurrentRound().getNumber() + 1, blackCard, this.getPlayers().get(index));
                this.deal();
                this.sendMessage(" ");
                this.sendMessage(IRCFormat.BOLD + "Round " + this.getCurrentRound().getNumber() + IRCFormat.RESET + "!");
                this.sendMessage(IRCFormat.BOLD + this.getCurrentRound().getCzar().getUser().getNick() + IRCFormat.RESET + " is the card czar.");
                this.sendMessage(IRCFormat.BOLD + this.getCurrentRound().getBlackCard().getText());
                this.showCards();
                this.getCurrentRound().advanceStage();
                break;
        }
    }

    /**
     * Removes a CardPack from this Game. If sweep is true, any cards in players' hands will be removed if they belonged
     * to the removed pack. If cards are removed, new cards will be dealt, and the affected players will have their
     * hands shown to them.
     *
     * @param cp    CardPack to remove
     * @param sweep Remove cards in hands?
     * @return true if the pack was removed, false if otherwise
     */
    public boolean removeCardPack(final CardPack cp, final boolean sweep) {
        if (!this.deck.removeCardPack(cp)) return false;
        if (!sweep) return true;
        this.allPlayers.forEach(p -> {
            if (!p.getHand().removeCards(cp.getWhiteCards())) return;
            this.deal(p);
            p.getUser().sendNotice("Your hand has changed as a result of card pack changes. Here's your new hand!");
            this.showCards(p);
        });
        return true;
    }

    /**
     * Removes a Player from this game. If there are not enough Players to continue, the game will end.
     *
     * @param p Player to remove
     */
    public void removePlayer(final Player p) {
        synchronized (this.players) {
            if (!this.players.remove(p)) return;
        }
        this.sendMessage(IRCFormat.BOLD + p.getUser().getNick() + IRCFormat.RESET + " has left the game.");
        if (this.host.equals(p)) this.nextHost();
        this.update();
        if (this.getCurrentRound() != null) {
            if (!this.hasEnoughPlayers()) return;
            if (this.getCurrentRound().getCzar().equals(p)) {
                this.sendMessage(IRCFormat.BOLD + "The czar has left!" + IRCFormat.RESET + " Returning your cards and starting a new round.");
                this.getCurrentRound().returnCards();
                this.advanceStage();
                return;
            }
            if (this.getCurrentRound().hasAllPlaysMade() && this.getCurrentRound().getCurrentStage() == RoundStage.WAITING_FOR_PLAYERS) {
                this.getCurrentRound().advanceStage();
            }
        }
    }

    /**
     * Removes a Player from this game given his name.
     *
     * @param name Name of Player to remove
     */
    public void removePlayer(final String name) {
        this.removePlayer(this.getPlayer(name));
    }

    /**
     * Sends a message parsed through {@link #antiPing(String)} to the channel.
     *
     * @param message Message to send
     */
    public void sendMessage(final String message) {
        this.channel.sendMessage(this.antiPing(message));
    }

    /**
     * Adds old data about a player to a rejoining player.
     *
     * @param newPlayer Player that is rejoining
     * @return true if data was applied, false if otherwise
     */
    public boolean setOldUserData(final Player newPlayer) {
        final Player oldPlayer;
        synchronized (this.allPlayers) {
            if (this.allPlayers.contains(newPlayer)) {
                oldPlayer = this.allPlayers.get(this.allPlayers.indexOf(newPlayer));
            } else return false;
        }
        final Hand<WhiteCard> hand = newPlayer.getHand();
        hand.clearHand();
        hand.addCards(oldPlayer.getHand().getCards());
        final Hand<BlackCard> wins = newPlayer.getWins();
        wins.clearHand();
        wins.addCards(oldPlayer.getWins().getCards());
        synchronized (this.players) {
            this.players.add(oldPlayer);
        }
        return true;
    }

    /**
     * Displays the current card counts.
     */
    public void showCardCounts() {
        this.sendMessage(this.getCardCounts());
    }

    /**
     * Shows a Player his cards.
     *
     * @param p Player to show cards to
     */
    public void showCards(final Player p) {
        final StringBuilder sb = new StringBuilder();
        final Hand<WhiteCard> hand = p.getHand();
        for (int i = 0; i < hand.getSize(); i++) {
            final WhiteCard wc = hand.getCard(i);
            sb.append(i + 1).append(". ").append(IRCFormat.BOLD).append(wc.getText()).append(IRCFormat.RESET).append(" ");
        }
        p.getUser().sendNotice(sb.toString());
    }

    /**
     * Shows each Player his hand.
     */
    public void showCards() {
        this.players.stream().filter(p -> !p.equals(this.getCurrentRound().getCzar())).forEach(this::showCards);
    }

    /**
     * Sends a message to the game channel, declaring who the host is.
     */
    public void showHost() {
        this.sendMessage("The host is " + IRCFormat.BOLD + this.host.getUser().getNick() + IRCFormat.RESET + ".");
    }

    /**
     * Shows the ordered scores in the game channel.
     */
    public void showScores() {
        final Map<Player, Integer> scores = new HashMap<>();
        synchronized (this.allPlayers) {
            this.allPlayers.stream().forEach(p -> scores.put(p, p.getScore()));
        }
        final Map<Player, Integer> sortedScores = new TreeMap<>(new DescendingValueComparator(scores));
        sortedScores.putAll(scores);
        final StringBuilder sb = new StringBuilder();
        sb.append(IRCFormat.BOLD + "Scores:").append(IRCFormat.RESET).append(" ");
        for (final Map.Entry<Player, Integer> entry : sortedScores.entrySet()) {
            sb.append(entry.getKey().getUser().getNick()).append(": ").append(entry.getValue() == null ? 0 : entry.getValue()).append(", ");
        }
        this.sendMessage(sb.toString().substring(0, sb.length() - 2));
    }

    /**
     * Starts the game.
     */
    public void start() {
        if (this.gameStatus != GameStatus.IDLE) return;
        this.advanceStage();
    }

    /**
     * Stops the game.
     */
    public void stop() {
        this.humanity.getGames().remove(this.channel);
        if (this.host != null && !this.hostWasVoiced) {
            this.humanity.getBot().sendRawLine("MODE " + this.channel.getName() + " -v " + this.host.getUser().getNick());
        }
        if (this.countdownTask != null) this.countdownTask.cancel(true);
        if (this.gameStatus != GameStatus.IDLE) {
            this.gameStatus = GameStatus.IDLE;
            this.sendMessage(IRCFormat.BOLD + "The game has ended.");
            if (this.gameStatus != GameStatus.JOINING) this.showScores();
        }
        this.gameStatus = GameStatus.ENDED;
    }

    /**
     * Updates the Channel and Users.
     */
    public void update() {
        this.updateChannel();
        this.updateUsers();
    }

    /**
     * Updates the stored Channel snapshot.
     */
    public void updateChannel() {
        this.setChannel(
            this.humanity.getBot().getChannels().stream()
                .filter(c -> c.getName().equalsIgnoreCase(this.getChannel().getName()))
                .findFirst()
                .orElseThrow(IllegalStateException::new)
        );
    }

    /**
     * Updates the Users stored in all Players.
     */
    public void updateUsers() {
        this.getHistoricPlayers().stream()
            .forEach(p -> {
                    final User newUser = this.channel.getUsers().keySet().stream()
                        .filter(u -> u.getNick().equalsIgnoreCase(p.getUser().getNick()))
                        .findFirst()
                        .orElse(null);
                    if (newUser == null) return;
                    p.setUser(newUser);
                }
            );

    }

    public enum GameStatus {
        /**
         * The game is not started. Players are not joining. Nothing is happening.
         */
        IDLE,
        /**
         * The game is started. Players are joining.
         */
        JOINING,
        /**
         * The game is now in play. Cards are being played.
         */
        PLAYING,
        /**
         * The game has terminated. Players are not joining. Nothing is happening.
         */
        ENDED
    }

    private class GameCountdown implements Runnable {

        private int runCount = 3; // 3 default

        @Override
        public void run() {
            final int seconds = this.runCount * 15;
            if (seconds > 0) {
                Game.this.sendMessage(IRCFormat.BOLD.toString() + (this.runCount * 15) + IRCFormat.RESET + " seconds remain to join the game!");
            }
            if (this.runCount-- < 1) {
                if (Game.this.getPlayers().size() >= 3) {
                    Game.this.advanceStage();
                } else {
                    Game.this.sendMessage(IRCFormat.BOLD + "Not enough players." + IRCFormat.RESET + " At least three people are required for the game to begin.");
                    Game.this.stop();
                }
                Game.this.countdownTask.cancel(true);
            }
        }
    }

    private class DescendingValueComparator implements Comparator<Player> {

        private final Map<Player, Integer> base;

        private DescendingValueComparator(final Map<Player, Integer> base) {
            this.base = base;
        }

        @Override
        public int compare(final Player o1, final Player o2) {
            return this.base.get(o1) >= this.base.get(o2) ? -1 : 1;
        }
    }
}
