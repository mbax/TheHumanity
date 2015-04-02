package org.royaldev.thehumanity.commands.impl.game;

import com.google.common.base.Preconditions;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.ParentCommand;
import org.royaldev.thehumanity.commands.impl.game.subcommands.AddPackSubcommand;
import org.royaldev.thehumanity.commands.impl.game.subcommands.HouseRulesSubCommand;
import org.royaldev.thehumanity.commands.impl.game.subcommands.PacksSubcommand;
import org.royaldev.thehumanity.commands.impl.game.subcommands.RemovePackSubcommand;

import java.util.Arrays;

@Command(
    name = "game",
    description = "Manages and displays various aspects of games.",
    usage = "<command> (subcommand) (arguments)"
)
public class GameCommand extends ParentCommand {

    public GameCommand(final TheHumanity instance) {
        Preconditions.checkNotNull(instance, "instance was null");
        Arrays.asList(
            new PacksSubcommand(instance),
            new AddPackSubcommand(instance),
            new RemovePackSubcommand(instance),
            new HouseRulesSubCommand(instance)
        ).forEach(this::addSubcommand);
    }
}
