package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.game.Game;
import org.royaldev.thehumanity.game.HouseRule;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Player;

@Command(
    name = "neverhaveiever",
    description = "Used to discard a card that the player doesn't know the meaning of.",
    aliases = {"nhie", "never"},
    usage = "<command> [card]"
)
public class NeverHaveIEverCommand extends InGameCommand {

    public NeverHaveIEverCommand(final TheHumanity instance) {
        super(instance);
    }

    @Override
    public void onInGameCommand(@NotNull final ActorEvent<User> event, final CallInfo ci, @NotNull final Game game, @NotNull final Player player, @NotNull final String[] args) {
        final User u = player.getUser();
        if (!game.hasHouseRule(HouseRule.NEVER_HAVE_I_EVER)) {
            this.notice(u, "This command may only be used when the " + HouseRule.NEVER_HAVE_I_EVER + " house rule is enabled.");
            return;
        }
        if (args.length < 1) {
            this.notice(u, "Please specify a card.");
            return;
        }
        int index;
        try {
            index = Integer.parseInt(args[0]);
        } catch (final NumberFormatException ex) {
            this.notice(u, "Invalid number.");
            return;
        }
        index--;
        if (index < 0 || index >= player.getHand().getSize()) {
            this.notice(u, "Invalid card.");
            return;
        }
        final WhiteCard unknown = player.getHand().getCard(index);
        player.getHand().removeCard(unknown);
        game.sendMessage(IRCFormat.BOLD + u.getNick() + IRCFormat.RESET + " doesn't know what " + IRCFormat.BOLD + unknown + IRCFormat.RESET + " means and has discarded that card.");
        game.deal(player);
        game.showCards(player);
    }
}
