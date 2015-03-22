package org.royaldev.thehumanity.commands.impl;

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
    public void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, final Game g, final String[] args) {
        final User u = event.getActor();
        if (args.length < 1) {
            this.notice(u, "Usage: " + this.getUsage().replace("<command>", ci.getLabel()));
            return;
        }
        final Player p = g.getPlayer(u);
        if (!this.isHostOrOp(p, g)) {
            this.notice(u, "You're not an op or the host!");
            return;
        }
        g.removePlayer(args[0]);
    }
}
