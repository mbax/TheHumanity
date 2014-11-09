package org.royaldev.thehumanity;

import org.ocpsoft.prettytime.PrettyTime;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

// TODO: Skip timeout

public class Game {

    private final TheHumanity humanity;
    private final Channel channel;
    /**
     * A list of the current players in this game.
     */
    private final List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
    /**
     * A list of all the players that have ever played in this game.
     */
    private final List<Player> allPlayers = Collections.synchronizedList(new ArrayList<Player>());
    private final Deck deck;
    private Round currentRound = null;
    private Player host = null;
    private ScheduledFuture countdownTask;
    private GameStatus gameStatus = GameStatus.IDLE;
    private long startTime;

    public Game(final TheHumanity humanity, final Channel channel, final List<CardPack> cardPacks) {
        this.humanity = humanity;
        this.channel = channel;
        this.deck = new Deck(cardPacks);
    }

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
        int totalCards = 0;
        for (final CardPack cp : this.getDeck().getCardPacks()) totalCards += cp.getWhiteCards().size();
        if (this.players.size() * 7 >= totalCards) {
            this.sendMessage(Colors.BOLD + "Not enough white cards to play!");
            this.stop();
            return;
        }
        this.deal(player);
        if (this.gameStatus != GameStatus.JOINING) this.showCards(player);
        this.sendMessage(Colors.BOLD + player.getUser().getNick() + Colors.NORMAL + " has joined the game!");
    }

    public void advanceStage() {
        switch (this.gameStatus) {
            case IDLE:
                this.gameStatus = GameStatus.JOINING;
                break;
            case JOINING:
                this.gameStatus = GameStatus.PLAYING;
                this.startTime = System.currentTimeMillis();
                break;
        }
        this.processStatus(this.gameStatus);
    }

    public String antiPing(String message) {
        for (final User u : this.channel.getUsers()) {
            if (u.getNick().length() <= 1) continue;
            message = message.replace(u.getNick(), u.getNick().substring(0, 1) + "\u200b" + u.getNick().substring(1));
        }
        return message;
    }

    public Player createPlayer(final User u) {
        if (this.hasPlayer(u.getNick())) return this.getPlayer(u.getNick());
        final Player p = new Player(u);
        this.addPlayer(p);
        return p;
    }

    public void deal(final Player player) {
        final Hand<WhiteCard> hand = player.getHand();
        while (hand.getSize() < 10) hand.addCard(this.getDeck().getRandomWhiteCard(null));
    }

    public void deal() {
        synchronized (this.players) {
            for (final Player p : this.players) this.deal(p);
        }
    }

    public Channel getChannel() {
        return this.channel;
    }

    public Round getCurrentRound() {
        return this.currentRound;
    }

    public Deck getDeck() {
        return this.deck;
    }

    public GameStatus getGameStatus() {
        return this.gameStatus;
    }

    public void setGameStatus(final GameStatus s) {
        this.gameStatus = s;
    }

    public Player getHost() {
        return this.host;
    }

    public void setHost(final Player host) {
        this.host = host;
        this.channel.send().setMode("+v " + this.getHost().getUser().getNick());
    }

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
        for (final Player p : this.getPlayers()) {
            if (this.humanity.usersMatch(u, p.getUser())) return p;
        }
        return null;
    }

    public Player getPlayer(final Player p) {
        return this.getPlayers().get(this.getPlayers().indexOf(p));
    }

    public Player getPlayer(final String name) {
        return this.getPlayer(this.channel.getBot().getUserChannelDao().getUser(name));
    }

    public List<Player> getPlayers() {
        synchronized (this.players) {
            return new ArrayList<>(this.players);
        }
    }

    public boolean hasEnoughPlayers() {
        if (this.getPlayers().size() < 3) {
            this.sendMessage(Colors.BOLD + "Not enough players to continue!");
            this.stop();
            return false;
        }
        return true;
    }

    public boolean hasPlayer(final String name) {
        return this.getPlayer(this.channel.getBot().getUserChannelDao().getUser(name)) != null;
    }

    public boolean hasPlayer(final Player p) {
        return this.players.contains(p);
    }

    public void nextHost() {
        if (this.host != null) this.channel.send().setMode("-v " + this.getHost().getUser().getNick());
        synchronized (this.players) {
            if (this.players.size() < 1) return; // should never happen without the game stopping
            this.setHost(this.players.get(0));
        }
    }

    public void processStatus(final GameStatus newStage) {
        if (newStage == GameStatus.IDLE) return;
        else if (newStage == GameStatus.JOINING) {
            this.countdownTask = this.humanity.getThreadPool().scheduleAtFixedRate(new GameCountdown(), 0L, 15L, TimeUnit.SECONDS);
        }
        switch (newStage) {
            case JOINING:
                final StringBuilder sb = new StringBuilder();
                sb.append(Colors.BOLD).append("Card packs for this game:").append(Colors.NORMAL).append(" ");
                for (final CardPack cp : this.getDeck().getCardPacks()) sb.append(cp.getName()).append(", ");
                this.sendMessage(Colors.BOLD + "A new game is starting!");
                this.sendMessage(sb.toString().substring(0, sb.length() - 2));
                this.sendMessage("Use " + Colors.BOLD + this.humanity.getPrefix() + "join" + Colors.NORMAL + " to join.");
                break;
            case PLAYING:
                if (!this.hasEnoughPlayers()) return;
                if (this.getCurrentRound() != null) this.showScores();
                int index = this.getCurrentRound() == null ? 0 : this.getPlayers().indexOf(this.getCurrentRound().getCzar()) + 1;
                if (index >= this.getPlayers().size()) index = 0;
                BlackCard blackCard = this.getDeck().getRandomBlackCard();
                while (blackCard.getBlanks() > 10 || blackCard.getBlanks() < 1) {
                    this.sendMessage("Black card " + Colors.BOLD + blackCard.getText() + Colors.NORMAL + " was skipped because it is invalid.");
                    blackCard = this.getDeck().getRandomBlackCard();
                }
                this.currentRound = new Round(this, this.getCurrentRound() == null ? 1 : this.getCurrentRound().getNumber() + 1, blackCard, this.getPlayers().get(index));
                this.deal();
                this.sendMessage(" ");
                this.sendMessage(Colors.BOLD + "Round " + this.getCurrentRound().getNumber() + Colors.NORMAL + "!");
                this.sendMessage(Colors.BOLD + this.getCurrentRound().getCzar().getUser().getNick() + Colors.NORMAL + " is the card czar.");
                this.sendMessage(Colors.BOLD + this.getCurrentRound().getBlackCard().getText());
                this.showCards();
                this.getCurrentRound().advanceStage();
                break;
        }
    }

    public void removePlayer(final Player p) {
        synchronized (this.players) {
            if (!this.players.remove(p)) return;
        }
        this.sendMessage(Colors.BOLD + p.getUser().getNick() + Colors.NORMAL + " has left the game.");
        if (this.host.equals(p)) this.nextHost();
        if (this.getCurrentRound().getCzar().equals(p)) {
            this.sendMessage(Colors.BOLD + "The czar has left!" + Colors.NORMAL + " Returning your cards and starting a new round.");
            this.getCurrentRound().returnCards();
            this.advanceStage();
            return;
        }
        if (this.getCurrentRound().hasAllPlaysMade() && this.getCurrentRound().getCurrentStage() == RoundStage.WAITING_FOR_PLAYERS) {
            this.getCurrentRound().advanceStage();
        }
    }

    public void removePlayer(final String name) {
        this.removePlayer(this.getPlayer(this.channel.getBot().getUserChannelDao().getUser(name)));
    }

    public void sendMessage(final String message) {
        this.channel.send().message(this.antiPing(message));
    }

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

    public void showCards(final Player u) {
        final StringBuilder sb = new StringBuilder();
        final Hand<WhiteCard> hand = u.getHand();
        for (int i = 0; i < hand.getSize(); i++) {
            final WhiteCard wc = hand.getCard(i);
            sb.append(i + 1).append(". ").append(Colors.BOLD).append(wc.getText()).append(Colors.NORMAL).append(" ");
        }
        u.getUser().send().notice(sb.toString());
    }

    public void showCards() {
        for (final Player p : this.players) {
            if (p.equals(this.getCurrentRound().getCzar())) continue;
            this.showCards(p);
        }
    }

    public void showScores() {
        final Map<Player, Integer> scores = new HashMap<>();
        synchronized (this.allPlayers) {
            for (final Player p : this.allPlayers) scores.put(p, p.getScore());
        }
        final Map<Player, Integer> sortedScores = new TreeMap<>(new DescendingValueComparator(scores));
        sortedScores.putAll(scores);
        final StringBuilder sb = new StringBuilder();
        sb.append(Colors.BOLD + "Scores:").append(Colors.NORMAL).append(" ");
        for (final Map.Entry<Player, Integer> entry : sortedScores.entrySet()) {
            sb.append(entry.getKey().getUser().getNick()).append(": ").append(entry.getValue() == null ? 0 : entry.getValue()).append(", ");
        }
        this.sendMessage(sb.toString().substring(0, sb.length() - 2));
    }

    public void start() {
        if (this.gameStatus != GameStatus.IDLE) return;
        this.advanceStage();
    }

    public void stop() {
        this.humanity.getGames().remove(this.channel);
        if (this.host != null) this.channel.send().setMode("-v " + this.getHost().getUser().getNick());
        if (this.countdownTask != null) this.countdownTask.cancel(true);
        if (this.gameStatus != GameStatus.IDLE) {
            this.gameStatus = GameStatus.IDLE;
            PrettyTime p = new PrettyTime();
            this.sendMessage(Colors.BOLD + "The game has ended. " + Colors.NORMAL + "Start time: " + p.format(new Date(System.currentTimeMillis() - this.startTime)));
            if (this.gameStatus != GameStatus.JOINING) this.showScores();
        }
        this.gameStatus = GameStatus.ENDED;
    }

    public enum GameStatus {
        IDLE,
        JOINING,
        PLAYING,
        ENDED
    }

    private class GameCountdown implements Runnable {

        private int runCount = 3; // 3 default

        @Override
        public void run() {
            final int seconds = this.runCount * 15;
            if (seconds > 0) {
                Game.this.sendMessage(Colors.BOLD + (this.runCount * 15) + Colors.NORMAL + " seconds remain to join the game!");
            }
            if (this.runCount-- < 1) {
                if (Game.this.getPlayers().size() >= 3) {
                    Game.this.advanceStage();
                } else {
                    Game.this.sendMessage(Colors.BOLD + "Not enough players. " + Colors.NORMAL + "At least three people are required for the game to begin.");
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
            if (this.base.get(o1) >= this.base.get(o2)) return -1;
            else return 1;
        }
    }
}
