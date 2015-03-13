package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;

@Command(
    name = "leave",
    description = "Leaves the game you're in.",
    aliases = {"leavegame", "part", "partgame"}
)
public class LeaveGameCommand extends InGameCommand {

    public LeaveGameCommand(final TheHumanity instance) {
        super(instance);
    }

    @Override
    public void onInGameCommand(final GenericMessageEvent event, final CallInfo ci, final Game g, final String[] args) {
        g.removePlayer(g.getPlayer(event.getUser()));
    }
}
