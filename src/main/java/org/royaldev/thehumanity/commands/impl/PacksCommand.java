package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.Colors;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.IRCCommand;

@Command(
    name = "packs",
    description = "Shows the loaded card packs."
)
public class PacksCommand extends IRCCommand {

    private final TheHumanity humanity;

    public PacksCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public void onCommand(final GenericMessageEvent event, final CallInfo ci, final String[] args) {
        final StringBuilder sb = new StringBuilder();
        for (final CardPack cp : this.humanity.getLoadedCardPacks()) {
            sb.append(Colors.BOLD).append(cp.getName()).append(Colors.NORMAL).append(", ");
        }
        event.respond(sb.substring(0, sb.length() - 2));
    }
}
