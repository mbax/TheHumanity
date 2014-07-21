package org.royaldev.thehumanity;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.royaldev.thehumanity.cards.Card.BlackCard;
import org.royaldev.thehumanity.cards.Card.WhiteCard;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.cards.Play;

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
// TODO: Fix only one card being removed for multi-card plays (aesthetically, apparently)

public class Game {

    private final TheHumanity humanity;
    private final Channel channel;
    private final List<User> users = Collections.synchronizedList(new ArrayList<User>());
    private final List<CardPack> cardPacks = Collections.synchronizedList(new ArrayList<CardPack>());
    private final List<WhiteCard> whiteCards = new ArrayList<>();
    private final List<BlackCard> blackCards = new ArrayList<>();
    private final Map<User, List<WhiteCard>> hands = new HashMap<>();
    private final Map<User, List<BlackCard>> winnings = new HashMap<>();
    private final List<Play> plays = new ArrayList<>();
    private final List<User> skipping = new ArrayList<>();
    private int roundNumber = 0;
    private User host = null;
    private User czar = null;
    private BlackCard currentBlackCard = null;
    private ScheduledFuture countdownTask;
    private Status status = Status.IDLE;

    public Game(final TheHumanity humanity, final Channel channel, final List<CardPack> cardPacks) {
        this.humanity = humanity;
        this.channel = channel;
        this.cardPacks.addAll(cardPacks);
        this.repopulateWhiteCards();
        this.repopulateBlackCards();
    }

    public void repopulateWhiteCards() {
        for (final CardPack cp : this.cardPacks) {
            thisCard:
            for (final WhiteCard wc : cp.getWhiteCards()) {
                for (final List<WhiteCard> lwc : this.hands.values()) {
                    if (lwc.contains(wc)) continue thisCard;
                }
                this.whiteCards.add(wc);
            }
        }
    }

    public void repopulateBlackCards() {
        for (final CardPack cp : this.cardPacks) {
            this.blackCards.addAll(cp.getBlackCards());
        }
    }

    public void setOldUserData(User newUser) {
        final List<WhiteCard> hand = new ArrayList<>();
        for (Map.Entry<User, List<WhiteCard>> entry : this.hands.entrySet()) {
            if (!this.humanity.usersMatch(entry.getKey(), newUser)) continue;
            hand.addAll(entry.getValue());
            this.hands.remove(entry.getKey());
            break;
        }
        final List<BlackCard> wins = new ArrayList<>();
        for (Map.Entry<User, List<BlackCard>> entry : this.winnings.entrySet()) {
            if (!this.humanity.usersMatch(entry.getKey(), newUser)) continue;
            wins.addAll(entry.getValue());
            this.winnings.remove(entry.getKey());
            break;
        }
        this.hands.put(newUser, hand);
        this.winnings.put(newUser, wins);
    }

    public List<User> getUsers() {
        synchronized (this.users) {
            return new ArrayList<>(this.users);
        }
    }

    public boolean hasUser(String name) {
        return this.hasUser(this.channel.getBot().getUserChannelDao().getUser(name));
    }

    public boolean hasUser(User u) {
        for (final User u2 : this.users) {
            if (this.humanity.usersMatch(u, u2)) return true;
        }
        return false;
    }

    public User getUser(String name) {
        return this.getUser(this.channel.getBot().getUserChannelDao().getUser(name));
    }

    /**
     * You wouldn't think this method would be necessary, but it is. Useful for
     * {@link org.pircbotx.snapshot.UserSnapshot}s.
     *
     * @param u User to get
     * @return Real user
     */
    public User getUser(User u) {
        for (final User u2 : this.users) {
            if (this.humanity.usersMatch(u, u2)) return u2;
        }
        return null;
    }

    public void addUser(final User u) {
        this.setOldUserData(u); // in case we have data from before
        synchronized (this.users) {
            if (this.hasUser(u)) return;
            this.users.add(u);
        }
        int totalCards = 0;
        for (final CardPack cp : this.cardPacks) totalCards += cp.getWhiteCards().size();
        if (this.users.size() * 7 >= totalCards) {
            this.sendMessage(Colors.BOLD + "Not enough white cards to play!");
            this.stop();
            return;
        }
        this.deal(u);
        if (this.status != Status.JOINING) this.showCards(u);
        this.sendMessage(Colors.BOLD + u.getNick() + Colors.NORMAL + " has joined the game!");
    }

    public void removeUser(User u) {
        u = this.getUser(u); // get REAL user
        synchronized (this.users) {
            this.users.remove(u);
        }
        this.sendMessage(Colors.BOLD + u.getNick() + Colors.NORMAL + " has left the game.");
        if (this.host.equals(u)) this.nextHost();
        if (this.czar.equals(u)) {
            this.czar = null;
            this.sendMessage(Colors.BOLD + "The czar has left!" + Colors.NORMAL + " Returning your cards and starting a new round.");
            for (final Play p : this.plays) {
                if (p.getPlayer() == null) continue;
                final List<WhiteCard> hand = this.getHand(p.getPlayer());
                hand.addAll(p.getWhiteCards());
                this.hands.put(p.getPlayer(), hand);
            }
            if (this.status == Status.WAITING_FOR_PLAYERS) this.advanceStage();
            if (this.status == Status.WAITING_FOR_CZAR) this.advanceStage();
        }
        if (this.users.size() < 3) {
            this.sendMessage(Colors.BOLD + "Not enough players to continue!");
            this.stop();
            return;
        }
        if (this.czar == null || this.czar.equals(u)) return; // don't doubly advance the stage
        if (this.allPlaysMade()) this.advanceStage();
    }

    public void removeUser(final String name) {
        this.removeUser(this.channel.getBot().getUserChannelDao().getUser(name));
    }

    public void skip(String name) {
        this.skip(this.channel.getBot().getUserChannelDao().getUser(name));
    }

    public void skip(User u) {
        u = this.getUser(u);
        if (this.skipping.contains(u)) return;
        this.skipping.add(u);
        if (this.allPlaysMade() & this.status != Status.WAITING_FOR_CZAR) this.advanceStage();
    }

    public boolean isSkipping(String name) {
        return this.isSkipping(this.channel.getBot().getUserChannelDao().getUser(name));
    }

    public boolean isSkipping(User u) {
        u = this.getUser(u);
        return this.skipping.contains(u);
    }

    public boolean allPlaysMade() {
        final List<User> usersTakingPart = new ArrayList<>(this.users);
        usersTakingPart.removeAll(this.skipping);
        return this.getPlays().size() >= usersTakingPart.size() - 1;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(final Status s) {
        this.status = s;
    }

    public List<CardPack> getCardPacks() {
        return this.cardPacks;
    }

    public User getCzar() {
        return this.czar;
    }

    public void addPlay(User u, int... indices) {
        final List<WhiteCard> hand = this.hands.get(u);
        if (hand == null) return;
        final List<WhiteCard> play = new ArrayList<>();
        for (int i : indices) {
            i--;
            if (i < 0 || i >= hand.size()) return;
            play.add(hand.get(i));
        }
        this.addPlay(u, play);
    }

    public void addPlay(User u, List<WhiteCard> wc) {
        if (this.hasPlayed(u)) return;
        final List<WhiteCard> hand = this.getHand(u);
        hand.removeAll(wc);
        this.hands.put(u, hand);
        this.plays.add(new Play(u, wc));
        u.send().notice("Card" + (wc.size() == 1 ? "" : "s") + " played!");
        if (this.allPlaysMade()) this.advanceStage();
    }

    public boolean hasPlayed(User u) {
        for (final Play p : this.plays) {
            if (p.getPlayer().equals(u)) return true;
        }
        return false;
    }

    public List<WhiteCard> getHand(User u) {
        return this.hands.get(u);
    }

    public List<Play> getPlays() {
        return this.plays;
    }

    public BlackCard getCurrentBlackCard() {
        return this.currentBlackCard;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public User getHost() {
        return this.host;
    }

    public void setHost(User host) {
        this.host = host;
        this.channel.send().setMode("+v " + this.host.getNick());
    }

    public void nextHost() {
        if (this.host != null) this.channel.send().setMode("-v " + this.host.getNick());
        if (this.users.size() < 1) return;
        this.setHost(this.users.get(0));
    }

    public void chooseWinningPlay(int index) {
        index--;
        if (index < 0 || index >= this.plays.size()) return;
        final Play p = this.plays.get(index);
        this.addWinning(p.getPlayer(), this.currentBlackCard);
        this.sendMessage(Colors.NORMAL + "Play " + Colors.BOLD + (index + 1) + Colors.NORMAL + " by " + Colors.BOLD + p.getPlayer().getNick() + Colors.NORMAL + " wins!");
        this.advanceStage();
    }

    public void displayPlays() {
        for (int i = 0; i < this.plays.size(); i++) {
            final Play p = this.plays.get(i);
            this.sendMessage((i + 1) + ". " + this.currentBlackCard.fillInBlanks(p));
        }
    }

    public void addWinning(User u, BlackCard bc) {
        final List<BlackCard> wins = this.winnings.containsKey(u) ? this.winnings.get(u) : new ArrayList<BlackCard>();
        wins.add(bc);
        this.winnings.put(u, wins);
    }

    public void displayScores() {
        for (User u : this.users) if (!this.winnings.containsKey(u)) this.winnings.put(u, new ArrayList<BlackCard>());
        final Map<User, List<BlackCard>> sortedWinnings = new TreeMap<>(new CoolGuyComparator(this.winnings));
        sortedWinnings.putAll(this.winnings);
        final StringBuilder sb = new StringBuilder();
        sb.append(Colors.BOLD + "Scores:").append(Colors.NORMAL).append(" ");
        for (Map.Entry<User, List<BlackCard>> entry : sortedWinnings.entrySet()) {
            sb.append(entry.getKey().getNick()).append(": ").append(entry.getValue() == null ? 0 : entry.getValue().size()).append(", ");
        }
        this.sendMessage(sb.toString().substring(0, sb.length() - 2));
    }

    public String antiPing(String message) {
        for (final User u : this.channel.getUsers()) {
            if (u.getNick().length() <= 1) continue;
            message = message.replace(u.getNick(), u.getNick().substring(0, 1) + "\u200b" + u.getNick().substring(1));
        }
        return message;
    }

    public void sendMessage(String message) {
        this.channel.send().message(this.antiPing(message));
    }

    public void start() {
        if (this.status != Status.IDLE) return;
        this.advanceStage();
        this.countdownTask = this.humanity.getThreadPool().scheduleAtFixedRate(new GameCountdown(), 0L, 15L, TimeUnit.SECONDS);
    }

    public void stop() {
        this.humanity.getGames().remove(this.channel);
        if (this.host != null) this.channel.send().setMode("-v " + this.host.getNick());
        if (this.countdownTask != null) this.countdownTask.cancel(true);
        if (this.status != Status.IDLE) {
            this.status = Status.IDLE;
            this.sendMessage(Colors.BOLD + "The game has ended.");
            if (this.status != Status.JOINING) this.displayScores();
        }
    }

    public WhiteCard getRandomWhiteCard() {
        if (this.whiteCards.size() < 1) {
            this.repopulateWhiteCards();
            this.sendMessage(Colors.BOLD + "White cards have run out!" + Colors.NORMAL + " Reshuffling used cards.");
        }
        Collections.shuffle(this.whiteCards);
        final WhiteCard wc = this.whiteCards.get(0);
        this.whiteCards.remove(wc);
        return wc;
    }

    public BlackCard getRandomBlackCard() {
        if (this.blackCards.size() < 1) {
            this.sendMessage(Colors.BOLD + "No more black cards!");
            this.stop();
            return null;
        }
        Collections.shuffle(this.blackCards);
        final BlackCard bc = this.blackCards.get(0);
        this.blackCards.remove(bc);
        return bc;
    }

    public void deal(final User u) {
        final List<WhiteCard> hand = this.hands.containsKey(u) ? this.hands.get(u) : new ArrayList<WhiteCard>();
        while (hand.size() < 7) hand.add(this.getRandomWhiteCard());
        this.hands.put(u, hand);
    }

    public void deal() {
        for (final User u : this.users) this.deal(u);
    }

    public void showCards(final User u) {
        final StringBuilder sb = new StringBuilder();
        final List<WhiteCard> hand = this.hands.get(u);
        for (int i = 0; i < hand.size(); i++) {
            final WhiteCard wc = hand.get(i);
            sb.append(i + 1).append(". ").append(Colors.BOLD).append(wc.getText()).append(Colors.NORMAL).append(" ");
        }
        u.send().notice(sb.toString());
    }

    public void showCards() {
        for (final User u : this.users) {
            if (u.equals(this.czar)) continue;
            this.showCards(u);
        }
    }

    public void processStatus(final Status newStage) {
        if (newStage == Status.IDLE) return;
        switch (newStage) {
            case JOINING:
                final StringBuilder sb = new StringBuilder();
                sb.append(Colors.BOLD).append("Card packs for this game:").append(Colors.NORMAL).append(" ");
                for (final CardPack cp : this.cardPacks) sb.append(cp.getName()).append(", ");
                this.sendMessage(Colors.BOLD + "A new game is starting!");
                this.sendMessage(sb.toString().substring(0, sb.length() - 2));
                this.sendMessage("Use " + Colors.BOLD + this.humanity.getPrefix() + "join" + Colors.NORMAL + " to join.");
                break;
            case WAITING_FOR_PLAYERS:
                this.roundNumber++;
                if (this.roundNumber != 1) this.displayScores();
                this.plays.clear();
                this.deal();
                int index = this.users.indexOf(this.czar) + 1;
                if (index >= this.users.size()) index = 0;
                this.czar = this.users.get(index);
                this.currentBlackCard = this.getRandomBlackCard();
                if (this.currentBlackCard == null) return;
                this.showCards();
                this.sendMessage(" ");
                this.sendMessage(Colors.BOLD + "Round " + this.roundNumber + Colors.NORMAL + "!");
                this.sendMessage(Colors.BOLD + this.czar.getNick() + Colors.NORMAL + " is the card czar.");
                this.sendMessage(Colors.BOLD + this.currentBlackCard.getText());
                break;
            case WAITING_FOR_CZAR:
                this.skipping.clear();
                if (this.czar == null) break;
                Collections.shuffle(this.plays);
                this.displayPlays();
                this.sendMessage(Colors.BOLD + this.czar.getNick() + Colors.NORMAL + " is picking a winner.");
                this.czar.send().notice("Send " + Colors.BOLD + this.humanity.getPrefix() + "pick" + Colors.NORMAL + " followed by the number you think should win.");
                break;
        }
    }

    public void advanceStage() {
        switch (this.status) {
            case IDLE:
                this.status = Status.JOINING;
                break;
            case JOINING:
                this.status = Status.WAITING_FOR_PLAYERS;
                break;
            case WAITING_FOR_PLAYERS:
                this.status = Status.WAITING_FOR_CZAR;
                break;
            case WAITING_FOR_CZAR:
                this.status = Status.WAITING_FOR_PLAYERS;
                break;
        }
        this.processStatus(this.status);
    }

    public enum Status {
        IDLE,
        JOINING,
        WAITING_FOR_PLAYERS,
        WAITING_FOR_CZAR
    }

    private class GameCountdown implements Runnable {
        private int runCount = 3; // 3 default

        @Override
        public void run() {
            final int seconds = runCount * 15;
            if (seconds > 0) {
                Game.this.sendMessage(Colors.BOLD + (runCount * 15) + Colors.NORMAL + " seconds remain to join the game!");
            }
            if (runCount-- < 1) {
                Game.this.countdownTask.cancel(true);
                if (Game.this.getUsers().size() >= 3) {
                    Game.this.advanceStage();
                } else {
                    Game.this.sendMessage(Colors.BOLD + "Not enough players. " + Colors.NORMAL + "At least three people are required for the game to begin.");
                    Game.this.stop();
                }
            }
        }
    }

    private class CoolGuyComparator implements Comparator<User> {

        private Map<User, List<BlackCard>> base;

        private CoolGuyComparator(Map<User, List<BlackCard>> base) {
            this.base = base;
        }

        @Override
        public int compare(User o1, User o2) {
            if (this.base.get(o1).size() >= this.base.get(o2).size()) return -1;
            else return 1;
        }
    }
}
