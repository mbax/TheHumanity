package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class RebootTheUniverseCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public RebootTheUniverseCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rbtu", "reboot"};
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    @Override
    public String getDescription() {
        return "Allows players to sacrifice one point to return cards to the deck and draw back to ten cards.";
    }

    @Override
    public String getName() {
        return "reboottheuniverse";
    }

    @Override
    public String getUsage() {
        return "<command> [card#...]";
    }

    @Override
    public void onCommand(final GenericMessageEvent event, final CallInfo ci, final String[] args) {
        final User u = event.getUser();
        if (args.length < 1) {
            this.notice(u, "Usage: " + this.getUsage().replace("<command>", ci.getLabel()));
            return;
        }
        final Game g = this.humanity.getGameFor(u);
        if (g == null) {
            this.notice(u, "You're not in a game.");
            return;
        }
        final Player p = g.getPlayer(u);
        if (p.getScore() < 1) {
            this.notice(u, "You must have points to use this command.");
            return;
        }
        final List<WhiteCard> cardsToRemove = new ArrayList<>();
        for (final String index : args) {
            try {
                cardsToRemove.add(p.getHand().getCard(index));
            } catch (final NumberFormatException ex) {
                this.notice(u, index + " is not a number. Please specify only card numbers separated by spaces. No cards or points have been removed.");
                return;
            }
        }
        p.getHand().removeCards(cardsToRemove);
        g.deal(p);
        p.getWins().removeCard(p.getWins().getCard(0));
        this.notice(u, "In exchange for a point, you have replaced " + cardsToRemove.size() + " cards with new ones.");
    }
}
