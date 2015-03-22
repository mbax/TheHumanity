package org.royaldev.thehumanity.commands.impl.game;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.HouseRule;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Player;
import org.royaldev.thehumanity.util.ConversionHelper;

import java.util.Arrays;

@Command(
    name = "houserules",
    description = "Manages house rules.",
    aliases = {"houserule", "hr", "rules"}
)
public class HouseRulesSubCommand extends InGameCommand {

    public HouseRulesSubCommand(final TheHumanity instance) {
        super(instance);
    }

    private void add(final ActorEvent<User> event, final CallInfo ci, final Game g, final String[] args) {
        final User u = event.getActor();
        final Player p = g.getPlayer(u);
        if (!this.isHostOrOp(p)) {
            this.notice(u, "You must be either the host or an operator to add house rules.");
            return;
        }
        if (args.length < 1) {
            this.notice(u, "Please provide a house rule.");
            return;
        }
        final HouseRule hr = this.getHouseRule(StringUtils.join(args, ' '));
        if (hr == null) {
            this.notice(u, "No such house rule.");
            return;
        }
        if (g.hasHouseRule(hr)) {
            this.notice(u, "This house rule is already being used.");
            return;
        }
        g.addHouseRule(hr);
        ConversionHelper.respond(event, "Added house rule: " + hr + ".");
    }

    private HouseRule getHouseRule(final String name) {
        try {
            return HouseRule.valueOf(name.toUpperCase());
        } catch (final IllegalArgumentException ex) {
            return HouseRule.getByFriendlyName(name);
        }
    }

    private void list(final ActorEvent<User> event, final Game g) {
        final StringBuilder sb = new StringBuilder();
        Arrays.stream(HouseRule.values()).sorted().forEach(hr -> {
            sb.append(g.hasHouseRule(hr) ? IRCFormat.GREEN : IRCFormat.RED);
            sb.append(hr);
            sb.append(IRCFormat.RESET).append(", ");
        });
        ConversionHelper.respond(event, sb.substring(0, sb.length() - 2));
    }

    private void remove(final ActorEvent<User> event, final CallInfo ci, final Game g, final String[] args) {
        final User u = event.getActor();
        final Player p = g.getPlayer(u);
        if (!this.isHostOrOp(p)) {
            this.notice(u, "You must be either the host or an operator to remove house rules.");
            return;
        }
        if (args.length < 1) {
            this.notice(u, "Please provide a house rule.");
            return;
        }
        final HouseRule hr = this.getHouseRule(StringUtils.join(args, ' '));
        if (hr == null) {
            this.notice(u, "No such house rule.");
            return;
        }
        if (!g.hasHouseRule(hr)) {
            this.notice(u, "This house rule is not being used.");
            return;
        }
        g.removeHouseRule(hr);
        ConversionHelper.respond(event, "Removed house rule: " + hr + ".");
    }

    @Override
    public void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, final Game g, final String[] args) {
        final User u = event.getActor();
        if (args.length < 1) {
            this.notice(u, "Not enough arguments.");
            this.notice(u, "Subcommands: (add, a), (remove, r), (list, l)");
            return;
        }
        final String[] newArgs = ArrayUtils.subarray(args, 1, args.length);
        switch (args[0].toLowerCase()) {
            case "add":
            case "a":
                this.add(event, ci, g, newArgs);
                break;
            case "remove":
            case "r":
                this.remove(event, ci, g, newArgs);
                break;
            case "list":
            case "l":
                this.list(event, g);
                break;
            default:
                this.notice(u, "Unknown subcommand.");
                break;
        }
    }
}
