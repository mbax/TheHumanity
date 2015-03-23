package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Player;

@Command(
    name = "kick",
    description = "Kicks a player from the game.",
    aliases = {"k"},
    usage = "<command> [player]"
)
public class KickCommand extends InGameCommand {

    public KickCommand(final TheHumanity instance) {
        super(instance);
    }

    @Override
    public void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, @NotNull final Game game, @NotNull final Player player, final String[] args) {
        final User u = player.getUser();
        if (args.length < 1) {
            this.notice(u, "Usage: " + this.getUsage().replace("<command>", ci.getLabel()));
            return;
        }
        final Player p = game.getPlayer(u);
        if (!this.isHostOrOp(p, game)) {
            this.notice(u, "You're not an op or the host!");
            return;
        }
        game.removePlayer(args[0]);
    }
}
