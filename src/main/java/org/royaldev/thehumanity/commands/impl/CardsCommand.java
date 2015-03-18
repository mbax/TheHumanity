package org.royaldev.thehumanity.commands.impl;

import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;

@Command(
    name = "cards",
    description = "Shows your cards."
)
public class CardsCommand extends InGameCommand {

    public CardsCommand(final TheHumanity instance) {
        super(instance);
    }

    @Override
    public void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, final Game g, final String[] args) {
        final User u = event.getActor();
        g.showCards(g.getPlayer(u));
    }
}
