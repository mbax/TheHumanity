package org.royaldev.thehumanity.commands.impl.game;

import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;
import org.royaldev.thehumanity.util.ConversionHelper;

import java.util.stream.Collectors;

@Command(
    name = "packs",
    description = "Shows the loaded packs for this game.",
    aliases = {"p", "pack", "loadedpacks", "loadedpack"}
)
public class PacksSubcommand extends InGameCommand {

    public PacksSubcommand(final TheHumanity instance) {
        super(instance);
    }

    @Override
    public void onInGameCommand(final ActorEvent<User> event, final CallInfo ci, final Game g, final String[] args) {
        final String list = IRCFormat.BOLD + g.getDeck().getCardPacks().stream().map(CardPack::getName).collect(Collectors.joining(IRCFormat.RESET + ", " + IRCFormat.BOLD));
        ConversionHelper.respond(event, list);
    }
}
