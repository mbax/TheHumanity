package org.royaldev.thehumanity.commands.impl;

import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.Round;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Player;

@Command(
    name = "skip",
    description = "Skips a player for the round.",
    usage = "<command> [player]"
)
public class SkipCommand extends InGameCommand {

    public SkipCommand(final TheHumanity instance) {
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
        final Player t = g.getPlayer(args[0]);
        if (t == null) {
            this.notice(u, "That person is not playing in this game.");
            return;
        }
        final Round r = g.getCurrentRound();
        if (!u.getNick().equalsIgnoreCase(args[0]) && !this.isHostOrOp(p, g)) {
            this.notice(u, "You're not an op, the host, or skipping yourself!");
            return;
        }
        if (r.isSkipped(t)) {
            this.notice(u, "That user is already skipped.");
            return;
        }
        this.notice(u, r.skip(t) ? "User skipped." : "User could not be skipped.");
    }
}
