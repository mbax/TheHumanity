package org.royaldev.thehumanity.commands.impl.game;

import org.pircbotx.Colors;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.InGameCommand;

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
    public void onInGameCommand(final GenericMessageEvent event, final CallInfo ci, final Game g, final String[] args) {
        final String list = Colors.BOLD + g.getDeck().getCardPacks().stream().map(CardPack::getName).collect(Collectors.joining(Colors.NORMAL + ", " + Colors.BOLD));
        event.respond(list);
    }
}
