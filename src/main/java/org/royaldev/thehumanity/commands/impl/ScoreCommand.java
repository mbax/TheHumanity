package org.royaldev.thehumanity.commands.impl;

import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;

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
    public void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, final Game g, final String[] args) {
        g.showScores();
    }
}
