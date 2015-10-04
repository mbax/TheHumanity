package org.royaldev.thehumanity.commands.impl.game.subcommands;

import com.google.common.base.Joiner;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.helper.ActorEvent;
import org.royaldev.thehumanity.game.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.packs.CardPack;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Player;
import org.royaldev.thehumanity.util.ConversionHelper;

@Command(
    name = "removepack",
    description = "Removes a card pack from the current game, optionally removing any dealt cards from the pack.",
    usage = "<command> [pack] (sweep)",
    aliases = {"rp"}
)
public class RemovePackSubcommand extends InGameCommand {

    public RemovePackSubcommand(final TheHumanity instance) {
        super(instance);
    }

    @Override
    public void onInGameCommand(@NotNull final ActorEvent<User> event, final CallInfo ci, @NotNull final Game game, @NotNull final Player player, @NotNull final String[] args) {
        final User u = player.getUser();
        if (!game.getHost().equals(player) && !this.humanity.hasChannelMode(game.getChannel(), u, 'o')) {
            this.notice(u, "You are not an op or the host!");
            return;
        }
        if (args.length < 1) {
            this.notice(u, "Not enough arguments.");
            return;
        }
        final CardPack cp = game.getDeck().getCardPacks().stream()
            .filter(c -> c.getName().equalsIgnoreCase(Joiner.on(' ').join(args)))
            .findFirst()
            .orElse(null);
        if (cp == null) {
            this.notice(u, "No such card pack.");
            return;
        }
        ConversionHelper.respond(event, (game.removeCardPack(cp, args.length > 1 && "sweep".equalsIgnoreCase(args[1])) ? "Removed" : "Could not remove") + " " + IRCFormat.BOLD + cp.getName() + IRCFormat.RESET + " from the game.");
    }
}
