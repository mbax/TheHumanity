package org.royaldev.thehumanity;

import org.pircbotx.Colors;
import org.royaldev.thehumanity.cards.Play;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Round {

    private final Game game;
    private final int number;
    private final BlackCard blackCard;
    private final Player czar;
    private final Set<Player> skippedPlayers = Collections.synchronizedSet(new HashSet<Player>());
    private final List<Play> plays = Collections.synchronizedList(new ArrayList<Play>());
    private RoundStage currentStage = RoundStage.IDLE;

    Round(final Game game, final int number, final BlackCard blackCard, final Player czar) {
        this.game = game;
        this.number = number;
        this.blackCard = blackCard;
        this.czar = czar;
    }

    /**
     * Processes various tasks for each new stage the round enters.
     */
    private void processStage() {
        if (!this.getGame().hasEnoughPlayers()) return;
        switch (this.currentStage) {
            case WAITING_FOR_CZAR:
                if (this.czar == null) break;
                Collections.shuffle(this.plays);
                this.displayPlays();
                this.getGame().sendMessage(Colors.BOLD + this.getCzar().getUser().getNick() + Colors.NORMAL + " is picking a winner.");
                this.getCzar().getUser().send().notice("Send " + Colors.BOLD + this.getGame().getHumanity().getPrefix() + "pick" + Colors.NORMAL + " followed by the number you think should win.");
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
        for (final WhiteCard wc : play.getWhiteCards()) play.getPlayer().getHand().removeCard(wc);
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

    /**
     * Chooses the winning play for this round. This ends the round.
     *
     * @param index Index of the winning play (starting at 1)
     */
    public void chooseWinningPlay(int index) {
        index--;
        if (index < 0 || index >= this.getPlays().size()) return;
        final Play p = this.getPlays().get(index);
        p.getPlayer().getWins().addCard(this.getBlackCard());
        this.getGame().sendMessage(Colors.NORMAL + "Play " + Colors.BOLD + (index + 1) + Colors.NORMAL + " by " + Colors.BOLD + p.getPlayer().getUser().getNick() + Colors.NORMAL + " wins!");
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
        return game;
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
        return this.getPlays().size() >= usersTakingPart.size() - 1;
    }

    /**
     * Checks if the given player has played in this round.
     *
     * @param p Player to check
     * @return true if played,false if otherwise
     */
    public boolean hasPlayed(final Player p) {
        for (final Play play : this.plays) {
            if (play.getPlayer().equals(p)) return true;
        }
        return false;
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
        for (final Play p : this.getPlays()) {
            for (final WhiteCard wc : p.getWhiteCards()) {
                p.getPlayer().getHand().addCard(wc);
            }
        }
    }

    /**
     * Skips a player. A skipped player will not need to submit a play in order for the stage to advance.
     *
     * @param p Player to skip
     */
    public boolean skip(final Player p) {
        if (this.isSkipped(p)) return false;
        synchronized (this.skippedPlayers) {
            this.skippedPlayers.add(p);
        }
        switch (this.getCurrentStage()) {
            case WAITING_FOR_PLAYERS:
                if (this.hasAllPlaysMade()) this.advanceStage();
                break;
            case WAITING_FOR_CZAR:
                if (p.equals(this.getCzar())) {
                    this.getGame().sendMessage(Colors.BOLD + "The czar has been skipped!" + Colors.NORMAL + " Returning your cards and starting a new round.");
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
