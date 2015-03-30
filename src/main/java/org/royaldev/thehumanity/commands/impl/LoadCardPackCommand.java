package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.packs.CardPack;
import org.royaldev.thehumanity.cards.packs.CardPackParser;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.NoticeableCommand;
import org.royaldev.thehumanity.util.ConversionHelper;

import java.util.List;
import java.util.stream.Collectors;

@Command(
    name = "loadcardpack",
    description = "Loads or reloads card packs into the list of loaded packs.",
    aliases = {"loadpack", "load"},
    usage = "<command> [packs...]"
)
public class LoadCardPackCommand extends NoticeableCommand {

    private final TheHumanity humanity;

    public LoadCardPackCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        if (args.length < 1) {
            this.notice(event.getActor(), "You must provide card packs.");
            return;
        }
        // Get the names of the requested packs
        final List<String> names = CardPackParser.getListOfCardPackNames(args, this.humanity.getDefaultPacks());
        // Get the corresponding card packs, filter out the nulls, then remove them
        names.stream()
            .map(this.humanity::getCardPack)
            .filter(pack -> pack != null)
            .forEach(this.humanity::removeCardPack);
        // Download or parse each pack again
        final List<CardPack> packs = names.stream()
            .map(this.humanity::parseOrDownloadCardPack)
            .collect(Collectors.toList());
        // Re-add them
        packs.forEach(this.humanity::addCardPack);
        ConversionHelper.respond(event, "Loaded " + packs.size() + " pack" + (packs.size() == 1 ? "" : "s") + ".");
    }
}
