package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.game.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.player.Player;

@Command(
    name = "score",
    description = "Displays the current score.",
    aliases = {"scores", "points", "point"}
)
public class ScoreCommand extends InGameCommand {

    public ScoreCommand(final TheHumanity instance) {
        super(instance);
    }

    @Override
    public void onInGameCommand(@NotNull final ActorEvent<User> event, final CallInfo ci, @NotNull final Game game, @NotNull final Player player, @NotNull final String[] args) {
        game.showScores();
    }
}
