package org.royaldev.thehumanity.commands.impl.game.subcommands;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.game.Game;
import org.royaldev.thehumanity.game.HouseRule;
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

    private void add(final ActorEvent<User> event, final Game g, final Player p, final String[] args) {
        final User u = p.getUser();
        if (!this.isHostOrOp(p)) {
            this.notice(u, "You must be either the host or an operator to add house rules.");
            return;
        }
        if (args.length < 1) {
            this.notice(u, "Please provide a house rule.");
            return;
        }
        final HouseRule hr = this.getHouseRule(Joiner.on(' ').join(args));
        if (hr == null) {
            this.notice(u, "No such house rule.");
            return;
        }
        if (g.hasHouseRule(hr)) {
            this.notice(u, "This house rule is already being used.");
            return;
        }
        g.addHouseRule(hr);
        ConversionHelper.respond(event, "Added house rule: " + IRCFormat.BOLD + hr + IRCFormat.RESET + ".");
    }

    @Nullable
    private HouseRule getHouseRule(@NotNull final String name) {
        Preconditions.checkNotNull(name, "name was null");
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

    private void remove(final ActorEvent<User> event, final Game g, final Player p, final String[] args) {
        final User u = p.getUser();
        if (!this.isHostOrOp(p)) {
            this.notice(u, "You must be either the host or an operator to remove house rules.");
            return;
        }
        if (args.length < 1) {
            this.notice(u, "Please provide a house rule.");
            return;
        }
        final HouseRule hr = this.getHouseRule(Joiner.on(' ').join(args));
        if (hr == null) {
            this.notice(u, "No such house rule.");
            return;
        }
        if (!g.hasHouseRule(hr)) {
            this.notice(u, "This house rule is not being used.");
            return;
        }
        g.removeHouseRule(hr);
        ConversionHelper.respond(event, "Removed house rule: " + IRCFormat.BOLD + hr + IRCFormat.RESET + ".");
    }

    @Override
    public void onInGameCommand(@NotNull final ActorEvent<User> event, final CallInfo ci, @NotNull final Game game, @NotNull final Player player, @NotNull final String[] args) {
        final User u = player.getUser();
        if (args.length < 1) {
            this.notice(u, "Not enough arguments.");
            this.notice(u, "Subcommands: (add, a), (remove, r), (list, l)");
            return;
        }
        final String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        switch (args[0].toLowerCase()) {
            case "add":
            case "a":
                this.add(event, game, player, newArgs);
                break;
            case "remove":
            case "r":
                this.remove(event, game, player, newArgs);
                break;
            case "list":
            case "l":
                this.list(event, game);
                break;
            default:
                this.notice(u, "Unknown subcommand.");
                break;
        }
    }
}
