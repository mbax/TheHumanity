package org.royaldev.thehumanity.commands.impl;

import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Player;

import java.util.ArrayList;
import java.util.List;

@Command(
    name = "reboottheuniverse",
    description = "Allows players to sacrifice one point to return cards to the deck and draw back to ten cards.",
    aliases = {"rbtu", "reboot"},
    usage = "<command> [card#...]"
)
public class RebootTheUniverseCommand extends InGameCommand {

    public RebootTheUniverseCommand(final TheHumanity instance) {
        super(instance);
    }

    /**
     * Gets WhiteCards from the given Player's Hand, given their numbers. If any number is invalid, null will be returned.
     *
     * @param args Numbers of cards
     * @param p    Player to get cards from
     * @param u    User that the Player represents
     * @return A list of WhiteCards of null
     */
    private List<WhiteCard> getCardsFromNumbers(final String[] args, final Player p, final User u) {
        final List<WhiteCard> cardsToRemove = new ArrayList<>();
        for (final String index : args) {
            try {
                cardsToRemove.add(p.getHand().getCard(index));
            } catch (final NumberFormatException ex) {
                this.notice(u, index + " is not a number. Please specify only card numbers separated by spaces. No cards or points have been removed.");
                return null;
            }
        }
        return cardsToRemove;
    }

    @Override
    public void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, final Game g, final String[] args) {
        final User u = event.getActor();
        if (args.length < 1) {
            this.notice(u, "Usage: " + this.getUsage().replace("<command>", ci.getLabel()));
            return;
        }
        final Player p = g.getPlayer(u);
        if (p.getScore() < 1) {
            this.notice(u, "You must have points to use this command.");
            return;
        }
        final List<WhiteCard> cardsToRemove = this.getCardsFromNumbers(args, p, u);
        if (cardsToRemove == null) return;
        p.getHand().removeCards(cardsToRemove);
        g.deal(p);
        p.getWins().removeCard(p.getWins().getCard(0));
        this.notice(u, "In exchange for a point, you have replaced " + cardsToRemove.size() + " cards with new ones.");
    }
}
