package org.royaldev.thehumanity.commands.impl.game;

import org.apache.commons.lang3.ArrayUtils;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.IRCCommand;
import org.royaldev.thehumanity.commands.InGameCommand;

import java.util.ArrayList;
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
        this.subcommands.add(new PacksSubcommand(this.humanity));
        this.subcommands.add(new AddPackSubcommand(this.humanity));
        this.subcommands.add(new RemovePackSubcommand(this.humanity));
    }

    private IRCCommand getSubcommand(final String name) {
        return this.subcommands.stream().filter(c -> c.getName().equalsIgnoreCase(name) || ArrayUtils.contains(c.getAliases(), name.toLowerCase())).findFirst().orElse(null);
    }

    @Override
    public void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, final Game g, final String[] args) {
        final User u = event.getActor();
        if (args.length < 1) {
            this.notice(u, "Provide a subcommand."); // TODO: Help
            return;
        }
        final IRCCommand subcommand = this.getSubcommand(args[0]);
        if (subcommand == null) {
            this.notice(u, "No such subcommand.");
            return;
        }
        subcommand.onCommand(event, ci, ArrayUtils.subarray(args, 1, args.length));
    }
}
