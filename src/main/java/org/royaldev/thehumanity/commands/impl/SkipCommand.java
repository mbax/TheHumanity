package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
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
    public void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, @NotNull final Game game, @NotNull final Player player, final String[] args) {
        final User u = player.getUser();
        if (args.length < 1) {
            this.notice(u, "Usage: " + this.getUsage().replace("<command>", ci.getLabel()));
            return;
        }
        final Player t = game.getPlayer(args[0]);
        if (t == null) {
            this.notice(u, "That person is not playing in this game.");
            return;
        }
        final Round r = game.getCurrentRound();
        if (r == null) {
            this.notice(u, "No round to skip in.");
            return;
        }
        if (!u.getNick().equalsIgnoreCase(args[0]) && !this.isHostOrOp(player, game)) {
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
