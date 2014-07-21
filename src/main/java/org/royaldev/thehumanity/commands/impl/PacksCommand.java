package org.royaldev.thehumanity.commands.impl;

import org.pircbotx.Colors;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.IRCCommand;

public class PacksCommand implements IRCCommand {

    private final TheHumanity humanity;

    public PacksCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        final StringBuilder sb = new StringBuilder();
        for (final CardPack cp : this.humanity.getLoadedCardPacks()) {
            sb.append(Colors.BOLD).append(cp.getName()).append(Colors.NORMAL).append(", ");
        }
        event.respond(sb.substring(0, sb.length() - 2));
    }

    @Override
    public String getName() {
        return "packs";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public String getDescription() {
        return "Shows the loaded card packs.";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }
}
