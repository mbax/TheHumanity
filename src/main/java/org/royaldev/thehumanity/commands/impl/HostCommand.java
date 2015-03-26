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
    name = "host",
    description = "Shows or sets the host, if you have permission.",
    usage = "<command> (nick)"
)
public class HostCommand extends InGameCommand {

    public HostCommand(final TheHumanity instance) {
        super(instance);
    }

    @Override
    public void onInGameCommand(@NotNull final ActorEvent<User> event, final CallInfo ci, @NotNull final Game game, @NotNull final Player player, @NotNull final String[] args) {
        if (args.length < 1) {
            game.showHost();
            return;
        }
        final User u = player.getUser();
        if (!this.isHostOrOp(player, game)) {
            this.notice(u, "You are not an op or the host!");
            return;
        }
        final Player t = game.getPlayer(args[0]);
        if (t == null) {
            this.notice(u, "No such nick is playing.");
            return;
        }
        game.setHost(t);
    }
}
