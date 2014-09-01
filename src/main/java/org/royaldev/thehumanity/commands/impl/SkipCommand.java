package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.Round;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.player.Player;

public class SkipCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public SkipCommand(TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    @Override
    public String getDescription() {
        return "Skips a player for the round.";
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getUsage() {
        return "<command> [player]";
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        final User u = event.getUser();
        if (args.length < 1) {
            this.notice(u, "Usage: " + this.getUsage().replace("<command>", ci.getLabel()));
            return;
        }
        final Game g = this.humanity.getGameFor(u);
        if (g == null) {
            this.notice(u, "You're not in any game.");
            return;
        }
        final Player p = g.getPlayer(u);
        final Player t = g.getPlayer(args[0]);
        if (t == null) {
            this.notice(u, "That person is not playing in this game.");
            return;
        }
        final Round r = g.getCurrentRound();
        if (!g.getChannel().getOps().contains(u) && !g.getHost().equals(p) && !u.getNick().equalsIgnoreCase(args[0])) {
            this.notice(u, "You're not an op, the host, or skipping yourself!");
            return;
        }
        if (r.isSkipped(t)) {
            this.notice(u, "That user is already skipped.");
            return;
        }
        if (r.skip(t)) this.notice(u, "User skipped.");
        else this.notice(u, "User could not be skipped.");
    }
}
