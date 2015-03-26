package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.Game.GameStatus;
import org.royaldev.thehumanity.HouseRule;
import org.royaldev.thehumanity.Round;
import org.royaldev.thehumanity.Round.RoundStage;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.Play;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Hand;
import org.royaldev.thehumanity.player.Player;

import java.util.ArrayList;
import java.util.List;

@Command(
    name = "pick",
    description = "Chooses a card.",
    aliases = {"pickcard", "p", "pc", "play", "playcard"},
    usage = "<command> [number]"
)
public class PickCardCommand extends InGameCommand {

    public PickCardCommand(final TheHumanity instance) {
        super(instance);
    }

    private void vote(final Game g, final User u, final Player p, final String[] args) {
        final Round r = g.getCurrentRound();
        if (r == null) {
            this.notice(u, "No round to vote in.");
            return;
        }
        final int winningPlay;
        try {
            winningPlay = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            this.notice(u, args[0] + " is not a valid number.");
            return;
        }
        final int index = winningPlay - 1;
        if (index < 0 || index >= r.getPlays().size()) {
            this.notice(u, "Invalid play.");
            return;
        }
        this.notice(u, r.hasVoted(p) ? "You have already voted!" : "Vote cast!");
        r.addVote(p, winningPlay);
    }

    /**
     * Processes any use of this command during the WAITING_FOR_CZAR stage.
     *
     * @param g    Game the command was used in
     * @param u    User using command
     * @param p    Player representative of the User
     * @param args Arguments supplied
     */
    private void czarPick(final Game g, final User u, final Player p, final String[] args) {
        final Round r = g.getCurrentRound();
        if (r == null) {
            this.notice(u, "No round to play in.");
            return;
        }
        if (g.hasHouseRule(HouseRule.GOD_IS_DEAD)) {
            this.vote(g, u, p, args);
            return;
        }
        if (!this.isCzar(r, p)) {
            this.notice(u, "You can't pick any cards right now.");
            return;
        }
        final int winningPlay;
        try {
            winningPlay = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            this.notice(u, args[0] + " is not a valid number.");
            return;
        }
        final int index = winningPlay - 1;
        this.notice(u, (index < 0 || index >= r.getPlays().size()) ? "Invalid play." : "Play picked!");
        r.chooseWinningPlay(winningPlay);
    }

    /**
     * Checks if the given Player is the czar of the given Round.
     *
     * @param r Round to check
     * @param p Player to check
     * @return true if Player is czar of the Round, false if otherwise
     */
    private boolean isCzar(final Round r, final Player p) {
        return p.equals(r.getCzar());
    }

    /**
     * Processes any use of this command during the WAITING_FOR_PLAYERS stage.
     *
     * @param g    Game the command was used in
     * @param u    User using command
     * @param p    Player representative of the User
     * @param args Arguments supplied
     */
    private void playerPick(final Game g, final User u, final Player p, final String[] args) {
        final Round r = g.getCurrentRound();
        if (r == null) {
            this.notice(u, "No round to play in.");
            return;
        }
        if (!g.hasHouseRule(HouseRule.GOD_IS_DEAD) && this.isCzar(r, p)) {
            this.notice(u, "You're the card czar! Wait until all the players have chosen their cards.");
            return;
        }
        if (r.hasPlayed(p)) {
            this.notice(u, "You've already played!");
            return;
        }
        if (args.length != r.getBlackCard().getBlanks()) {
            this.notice(u, IRCFormat.BOLD + "Wrong amount of cards!" + IRCFormat.RESET + " You need to pick exactly " + r.getBlackCard().getBlanks() + " cards.");
            return;
        }
        final Hand<WhiteCard> hand = p.getHand();
        final List<WhiteCard> play = this.processPicks(u, hand, args);
        if (play == null) return;
        this.notice(u, "Card" + (play.size() == 1 ? "" : "s") + " picked.");
        r.addPlay(new Play(p, play));
    }

    /**
     * Returns a list of WhiteCards from the given Hand, given their numbers as Strings.
     *
     * @param u     User who used command
     * @param hand  Hand to use cards from
     * @param picks Supplied numbers
     * @return List of WhiteCards or null if there was an issue
     */
    private List<WhiteCard> processPicks(final User u, final Hand<WhiteCard> hand, final String[] picks) {
        final List<WhiteCard> play = new ArrayList<>();
        for (final String number : picks) {
            int card;
            try {
                card = Integer.parseInt(number);
            } catch (NumberFormatException ex) {
                this.notice(u, number + " is not a valid number.");
                return null;
            }
            card--;
            if (card < 0 || card >= hand.getSize()) {
                this.notice(u, (card + 1) + " is not a valid choice.");
                return null;
            }
            final WhiteCard toPlay = hand.getCard(card);
            if (play.contains(toPlay)) {
                this.notice(u, "You cannot play the same card twice!");
                return null;
            }
            play.add(toPlay);
        }
        return play;
    }

    @Override
    public void onInGameCommand(@NotNull final ActorEvent<User> event, final CallInfo ci, @NotNull final Game game, @NotNull final Player player, @NotNull final String[] args) {
        final User u = player.getUser();
        if (args.length < 1) {
            this.notice(u, "Usage: " + this.getUsage().replace("<command>", ci.getLabel()));
            return;
        }
        if (game.getGameStatus() != GameStatus.PLAYING) {
            this.notice(u, "The game has not started!");
            return;
        }
        final Round r = game.getCurrentRound();
        if (r == null) {
            this.notice(u, "No round to play in.");
            return;
        }
        if (r.getCurrentStage() == RoundStage.WAITING_FOR_CZAR) {
            this.czarPick(game, u, player, args);
        } else if (r.getCurrentStage() == RoundStage.WAITING_FOR_PLAYERS) {
            this.playerPick(game, u, player, args);
        } else {
            this.notice(u, "You cannot pick a card right now.");
        }
    }
}
