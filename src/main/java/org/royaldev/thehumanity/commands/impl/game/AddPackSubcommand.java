package org.royaldev.thehumanity.commands.impl.game;

import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Player;

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
    public void onInGameCommand(final GenericMessageEvent event, final CallInfo ci, final Game g, final String[] args) {
        final User u = event.getUser();
        final Player p = g.getPlayer(u);
        if (!g.getChannel().getOps().contains(u) && !g.getHost().equals(p)) {
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
        event.respond((g.addCardPack(cp) ? "Added" : "Could not add") + " " + Colors.BOLD + cp.getName() + Colors.NORMAL + " to the game.");
    }
}
