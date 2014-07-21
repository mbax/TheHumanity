package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.Game.Status;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.Card.WhiteCard;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;

import java.util.ArrayList;
import java.util.List;

public class PickCardCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public PickCardCommand(final TheHumanity humanity) {
        this.humanity = humanity;
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
        if (g.getStatus() == Status.WAITING_FOR_CZAR) {
            if (!g.getCzar().equals(u)) {
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
            g.chooseWinningPlay(winningPlay);
        } else if (g.getStatus() == Status.WAITING_FOR_PLAYERS) {
            if (g.getCzar().equals(u)) {
                this.notice(u, "You're the card czar! Wait until all the players have chosen their cards.");
                return;
            }
            if (g.hasPlayed(u)) {
                this.notice(u, "You've already played!");
                return;
            }
            if (args.length != g.getCurrentBlackCard().getBlanks()) {
                this.notice(u, Colors.BOLD + "Wrong amount of cards!" + Colors.NORMAL + " You need to pick exactly " + g.getCurrentBlackCard().getBlanks() + " cards.");
                return;
            }
            final List<WhiteCard> hand = g.getHand(u);
            if (hand == null) {
                this.notice(u, "You have no hand!");
                return;
            }
            final List<WhiteCard> play = new ArrayList<>();
            for (String number : args) {
                int card;
                try {
                    card = Integer.parseInt(number);
                } catch (NumberFormatException ex) {
                    this.notice(u, number + " is not a valid number.");
                    return;
                }
                card--;
                if (card < 0 || card >= hand.size()) {
                    this.notice(u, (card + 1) + " is not a valid choice.");
                    return;
                }
                final WhiteCard toPlay = hand.get(card);
                if (play.contains(toPlay)) {
                    this.notice(u, "You cannot play the same card twice!");
                    return;
                }
                play.add(toPlay);
            }
            g.addPlay(u, play);
        } else {
            this.notice(u, "You cannot pick a card right now.");
        }
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
    public String getDescription() {
        return "Chooses a card.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pickcard", "p", "pc", "play", "playcard"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }
}
