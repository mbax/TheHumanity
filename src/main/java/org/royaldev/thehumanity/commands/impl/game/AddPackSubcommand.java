package org.royaldev.thehumanity.commands.impl.game;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Player;
import org.royaldev.thehumanity.util.ConversionHelper;

@Command(
    name = "addpack",
    description = "Adds a card pack to the current game.",
    aliases = {"ap"}
)
public class AddPackSubcommand extends InGameCommand {

    public AddPackSubcommand(final TheHumanity instance) {
        super(instance);
    }

    @Override
    public void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, @NotNull final Game game, @NotNull final Player player, final String[] args) {
        final User u = player.getUser();
        if (!game.getHost().equals(player) && !this.humanity.hasChannelMode(game.getChannel(), u, 'o')) {
            this.notice(u, "You are not an op or the host!");
            return;
        }
        if (args.length < 1) {
            this.notice(u, "Not enough arguments.");
            return;
        }
        final CardPack cp = this.humanity.getCardPack(args[0]);
        if (cp == null) {
            this.notice(u, "No such card pack.");
            return;
        }
        ConversionHelper.respond(event, (game.addCardPack(cp) ? "Added" : "Could not add") + " " + IRCFormat.BOLD + cp.getName() + IRCFormat.RESET + " to the game.");
    }
}
