package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
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
    public void onInGameCommand(final GenericMessageEvent event, final CallInfo ci, final Game g, final String[] args) {
        if (args.length < 1) {
            g.showHost();
            return;
        }
        final User u = event.getUser();
        final Player p = g.getPlayer(u);
        if (!g.getChannel().getOps().contains(u) && !g.getHost().equals(p)) {
            this.notice(u, "You are not an op or the host!");
            return;
        }
        final Player t = g.getPlayer(args[0]);
        if (t == null) {
            this.notice(u, "No such nick is playing.");
            return;
        }
        g.setHost(t);
    }
}
