package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.Game.GameStatus;
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

    private void czarPick(final Game g, final User u, final Player p, final String[] args) {
        final Round r = g.getCurrentRound();
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
        this.notice(u, "Play picked!");
        r.chooseWinningPlay(winningPlay);
    }

    private boolean isCzar(final Round r, final Player p) {
        return r.getCzar().equals(p);
    }

    private void playerPick(final Game g, final User u, final Player p, final String[] args) {
        final Round r = g.getCurrentRound();
        if (this.isCzar(r, p)) {
            this.notice(u, "You're the card czar! Wait until all the players have chosen their cards.");
            return;
        }
        if (r.hasPlayed(p)) {
            this.notice(u, "You've already played!");
            return;
        }
        if (args.length != r.getBlackCard().getBlanks()) {
            this.notice(u, Colors.BOLD + "Wrong amount of cards!" + Colors.NORMAL + " You need to pick exactly " + r.getBlackCard().getBlanks() + " cards.");
            return;
        }
        final Hand<WhiteCard> hand = p.getHand();
        final List<WhiteCard> play = this.processPicks(u, hand, args);
        if (play == null) return;
        this.notice(u, "Card" + (play.size() == 1 ? "" : "s") + " picked.");
        r.addPlay(new Play(p, play));
    }

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
    public void onInGameCommand(final GenericMessageEvent event, final CallInfo ci, final Game g, final String[] args) {
        final User u = event.getUser();
        if (args.length < 1) {
            this.notice(u, "Usage: " + this.getUsage().replace("<command>", ci.getLabel()));
            return;
        }
        if (g.getGameStatus() != GameStatus.PLAYING) {
            this.notice(u, "The game has not started!");
            return;
        }
        final Player p = g.getPlayer(u);
        final Round r = g.getCurrentRound();
        if (r.getCurrentStage() == RoundStage.WAITING_FOR_CZAR) {
            this.czarPick(g, u, p, args);
        } else if (r.getCurrentStage() == RoundStage.WAITING_FOR_PLAYERS) {
            this.playerPick(g, u, p, args);
        } else {
            this.notice(u, "You cannot pick a card right now.");
        }
    }
}
