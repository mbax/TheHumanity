package org.royaldev.thehumanity.commands.impl.game;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.IRCCommand;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(
    name = "game",
    description = "Manages and displays various aspects of games.",
    usage = "<command> (subcommand) (arguments)"
)
public class GameCommand extends InGameCommand {

    private final List<IRCCommand> subcommands = new ArrayList<>();

    public GameCommand(final TheHumanity instance) {
        super(instance);
        Arrays.asList(
            new PacksSubcommand(this.humanity),
            new AddPackSubcommand(this.humanity),
            new RemovePackSubcommand(this.humanity),
            new HouseRulesSubCommand(this.humanity)
        ).forEach(this.subcommands::add);
    }

    @NotNull
    private String getHelpString() {
        final StringBuilder sb = new StringBuilder("Subcommands: ");
        for (final IRCCommand subcommand : this.subcommands) {
            sb.append("(").append(subcommand.getName());
            for (final String alias : subcommand.getAliases()) {
                sb.append(", ").append(alias);
            }
            sb.append("), ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    private IRCCommand getSubcommand(@NotNull final String name) {
        Preconditions.checkNotNull(name, "name was null");
        return this.subcommands.stream().filter(c -> c.getName().equalsIgnoreCase(name) || Arrays.asList(c.getAliases()).contains(name.toLowerCase())).findFirst().orElse(null);
    }

    @Override
    public void onInGameCommand(@NotNull final ActorEvent<User> event, final CallInfo ci, @NotNull final Game game, @NotNull final Player player, @NotNull final String[] args) {
        final User u = player.getUser();
        if (args.length < 1) {
            this.notice(u, "Provide a subcommand.");
            this.notice(u, this.getHelpString());
            return;
        }
        final IRCCommand subcommand = this.getSubcommand(args[0]);
        if (subcommand == null) {
            this.notice(u, "No such subcommand.");
            return;
        }
        subcommand.onCommand(event, ci, Arrays.copyOfRange(args, 1, args.length));
    }
}
