package org.royaldev.thehumanity;

import org.kitteh.irc.client.library.IRCFormat;
import org.royaldev.thehumanity.cards.Play;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Round {

    private final Game game;
    private final int number;
    private final BlackCard blackCard;
    private final Player czar;
    private final Set<Player> skippedPlayers = Collections.synchronizedSet(new HashSet<>());
    private final List<Play> plays = Collections.synchronizedList(new ArrayList<>());
    private ScheduledFuture reminderTask;
    private RoundStage currentStage = RoundStage.IDLE;

    Round(final Game game, final int number, final BlackCard blackCard, final Player czar) {
        this.game = game;
        this.number = number;
        this.blackCard = blackCard;
        this.czar = czar;
    }

    private ScheduledFuture makeReminderTask() {
        return this.getGame().getHumanity().getThreadPool().scheduleAtFixedRate(
            () -> this.getGame().getChannel().sendMessage(this.getCzar().getUser().getNick() + ": Wake up! You're the czar!"),
            45000L,
            22500L,
            TimeUnit.MILLISECONDS
        );
    }

    /**
     * Processes tasks that house rules need to complete.
     */
    private void processHouseRules() {
        if (this.currentStage != RoundStage.WAITING_FOR_PLAYERS) return;
        if (this.game.hasHouseRule(HouseRule.RANDO_CARDRISSIAN)) {
            final List<WhiteCard> randoPlay = new ArrayList<>();
            for (int i = 0; i < this.blackCard.getBlanks(); i++) {
                randoPlay.add(this.game.getDeck().getRandomWhiteCard(null));
            }
            this.addPlay(new Play(this.game.getRandoCardrissian(), randoPlay));
        }
        if (this.game.hasHouseRule(HouseRule.PACKING_HEAT)) {
            this.game.getPlayers().forEach(p -> p.getHand().addCard(this.game.getDeck().getRandomWhiteCard(null)));
        }
    }

    /**
     * Processes various tasks for each new stage the round enters.
     */
    private void processStage() {
        if (!this.getGame().hasEnoughPlayers()) return;
        switch (this.currentStage) {
            case WAITING_FOR_PLAYERS:
                this.processHouseRules();
                this.game.showCards();
                break;
            case WAITING_FOR_CZAR:
                if (this.czar == null) break;
                Collections.shuffle(this.plays);
                this.displayPlays();
                this.getGame().sendMessage(IRCFormat.BOLD + this.getCzar().getUser().getNick() + IRCFormat.RESET + " is picking a winner.");
                this.getCzar().getUser().sendNotice("Send " + IRCFormat.BOLD + this.getGame().getHumanity().getPrefix() + "pick" + IRCFormat.RESET + " followed by the number you think should win.");
                this.reminderTask = this.makeReminderTask();
                break;
            case ENDED:
                this.getGame().advanceStage();
                break;
        }
    }

    /**
     * Adds a play to this round.
     *
     * @param play Play to add
     */
    public void addPlay(final Play play) {
        synchronized (this.plays) {
            this.plays.add(play);
        }
        play.getWhiteCards().stream().forEach(play.getPlayer().getHand()::removeCard);
        if (this.hasAllPlaysMade()) this.advanceStage();
    }

    /**
     * Progresses the stage to the next following stage.
     */
    public void advanceStage() {
        switch (this.currentStage) {
            case IDLE:
                this.currentStage = RoundStage.WAITING_FOR_PLAYERS;
                break;
            case WAITING_FOR_PLAYERS:
                this.currentStage = RoundStage.WAITING_FOR_CZAR;
                break;
            case WAITING_FOR_CZAR:
                this.currentStage = RoundStage.ENDED;
                break;
        }
        this.processStage();
    }

    public void cancelReminderTask() {
        if (this.reminderTask == null || this.reminderTask.isCancelled() || this.reminderTask.isDone()) {
            return;
        }
        this.reminderTask.cancel(true);
    }

    /**
     * Chooses the winning play for this round. This ends the round.
     *
     * @param index Index of the winning play (starting at 1)
     */
    public void chooseWinningPlay(int index) {
        this.cancelReminderTask();
        index--;
        if (index < 0 || index >= this.getPlays().size()) return;
        final Play p = this.getPlays().get(index);
        p.getPlayer().getWins().addCard(this.getBlackCard());
        this.getGame().sendMessage(IRCFormat.RESET + "Play " + IRCFormat.BOLD + (index + 1) + IRCFormat.RESET + " by " + IRCFormat.BOLD + p.getPlayer().getUser().getNick() + IRCFormat.RESET + " wins!");
        this.advanceStage();
    }

    /**
     * Displays all the plays made this round (without player names).
     */
    public void displayPlays() {
        for (int i = 0; i < this.getPlays().size(); i++) {
            final Play p = this.getPlays().get(i);
            this.getGame().sendMessage((i + 1) + ". " + this.getBlackCard().fillInBlanks(p));
        }
    }

    /**
     * Gets the plays that have been made by players still taking part in the game. Modifying this list will not change
     * it in the round.
     *
     * @return Cloned list of active-player plays
     */
    public List<Play> getActivePlayerPlays() { // TODO: Rename method?
        return this.plays.stream().filter(p -> this.getGame().getPlayers().contains(p.getPlayer())).collect(Collectors.toList());
    }

    /**
     * Gets the black card for this round.
     *
     * @return Black card
     */
    public BlackCard getBlackCard() {
        return this.blackCard;
    }

    /**
     * Gets the current stage this round is in.
     *
     * @return RoundStage
     */
    public RoundStage getCurrentStage() {
        return this.currentStage;
    }

    /**
     * Gets the czar for this round. This may change if the czar leaves.
     *
     * @return Czar
     */
    public Player getCzar() {
        return this.czar;
    }

    /**
     * Gets the game that this round is associated with.
     *
     * @return Game
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * Gets the number of this round.
     *
     * @return Number
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * Gets all of the plays that have been made this round. This is a copy of the list, not the real list. Modifying it
     * will not change it in the round.
     *
     * @return Cloned list of plays
     */
    public List<Play> getPlays() {
        synchronized (this.plays) {
            return new ArrayList<>(this.plays);
        }
    }

    /**
     * Gets all the skipped players for this round. Returns the actual list. Modifying the list returned will modify the
     * round.
     *
     * @return List of skipped players
     */
    public Set<Player> getSkippedPlayers() {
        synchronized (this.skippedPlayers) {
            return this.skippedPlayers;
        }
    }

    /**
     * Checks if this round has all necessary plays made to advance the stage (meaning that every player has played).
     * Skipped players are ignored.
     *
     * @return true if all plays have been made, false if otherwise
     */
    public boolean hasAllPlaysMade() {
        final List<Player> usersTakingPart = new ArrayList<>(this.getGame().getPlayers());
        usersTakingPart.removeAll(this.getSkippedPlayers());
        return this.getActivePlayerPlays().size() >= usersTakingPart.size() - 1;
    }

    /**
     * Checks if the given player has played in this round.
     *
     * @param p Player to check
     * @return true if played,false if otherwise
     */
    public boolean hasPlayed(final Player p) {
        return this.plays.stream().anyMatch(play -> play.getPlayer().equals(p));
    }

    /**
     * Checks if the given player is skipped.
     *
     * @param p Player to check
     * @return true if skipped, false if otherwise
     */
    public boolean isSkipped(final Player p) {
        return this.getSkippedPlayers().contains(p);
    }

    /**
     * Returns all the played cards back to the hands of the players.
     */
    public void returnCards() {
        this.getPlays().stream().forEach(p -> p.getWhiteCards().stream().forEach(p.getPlayer().getHand()::addCard));
    }

    /**
     * Skips a player. A skipped player will not need to submit a play in order for the stage to advance.
     *
     * @param p Player to skip
     */
    public boolean skip(final Player p) {
        if (this.isSkipped(p)) return false;
        // If the total amount of players less the skipped players is less than the amount needed to play, don't skip.
        // However, if this person is the czar, it's fine.
        if (!p.equals(this.getCzar()) && this.getGame().getPlayers().size() - this.skippedPlayers.size() < 3) {
            return false;
        }
        synchronized (this.skippedPlayers) {
            this.skippedPlayers.add(p);
        }
        switch (this.getCurrentStage()) {
            case WAITING_FOR_PLAYERS:
                if (this.hasAllPlaysMade()) this.advanceStage();
                break;
            case WAITING_FOR_CZAR:
                if (p.equals(this.getCzar())) {
                    this.getGame().sendMessage(IRCFormat.BOLD + "The czar has been skipped!" + IRCFormat.RESET + " Returning your cards and starting a new round.");
                    this.returnCards();
                    this.advanceStage();
                }
                break;
        }
        return true;
    }

    public enum RoundStage {
        /**
         * This round has not started.
         */
        IDLE,
        /**
         * The players are submitting their cards for this round.
         */
        WAITING_FOR_PLAYERS,
        /**
         * The czar is choosing a winner for this round.
         */
        WAITING_FOR_CZAR,
        /**
         * This round has ended.
         */
        ENDED
    }

}
