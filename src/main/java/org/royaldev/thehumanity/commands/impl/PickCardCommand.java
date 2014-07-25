package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.Round;
import org.royaldev.thehumanity.Round.RoundStage;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.Play;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.player.Hand;
import org.royaldev.thehumanity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class PickCardCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public PickCardCommand(final TheHumanity humanity) {
        this.humanity = humanity;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pickcard", "p", "pc", "play", "playcard"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    @Override
    public String getDescription() {
        return "Chooses a card.";
    }

    @Override
    public String getName() {
        return "pick";
    }

    @Override
    public String getUsage() {
        return "<command> [number]";
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        final User u = event.getUser();
        if (args.length < 1) {
            this.notice(u, "Usage: " + this.getUsage().replace("<command>", ci.getLabel()));
            return;
        }
        final Game g = this.humanity.getGameFor(u);
        if (g == null) {
            this.notice(u, "You're not in any game!");
            return;
        }
        final Player p = g.getPlayer(u);
        final Round r = g.getCurrentRound();
        if (r.getCurrentStage() == RoundStage.WAITING_FOR_CZAR) {
            if (!r.getCzar().equals(p)) {
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
        } else if (r.getCurrentStage() == RoundStage.WAITING_FOR_PLAYERS) {
            if (r.getCzar().equals(p)) {
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
            final List<WhiteCard> play = new ArrayList<>();
            for (final String number : args) {
                int card;
                try {
                    card = Integer.parseInt(number);
                } catch (NumberFormatException ex) {
                    this.notice(u, number + " is not a valid number.");
                    return;
                }
                card--;
                if (card < 0 || card >= hand.getSize()) {
                    this.notice(u, (card + 1) + " is not a valid choice.");
                    return;
                }
                final WhiteCard toPlay = hand.getCard(card);
                if (play.contains(toPlay)) {
                    this.notice(u, "You cannot play the same card twice!");
                    return;
                }
                play.add(toPlay);
            }
            this.notice(u, "Card" + (play.size() == 1 ? "" : "s") + " picked.");
            r.addPlay(new Play(p, play));
        } else {
            this.notice(u, "You cannot pick a card right now.");
        }
    }
}
