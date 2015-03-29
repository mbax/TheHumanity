package org.royaldev.thehumanity.commands.impl;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.ActorEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.IRCCommand;
import org.royaldev.thehumanity.util.ConversionHelper;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.stream.Collectors;

@Command(
    name = "packs",
    description = "Shows the loaded card packs."
)
public class PacksCommand extends IRCCommand {

    private static final DecimalFormat df = new DecimalFormat("0.##%");
    private static final Random r = new Random();
    private static final String GIST_ID = "thehumanity:packs";
    private final TheHumanity humanity;

    public PacksCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    /**
     * Generates Markdown, suitable for pasting to GitHub Gist, for the the given CardPack. Markdown will include the
     * name of the pack, any description or author (if set), statistics about the numbers of cards, and a random
     * selection of both one white and one black card.
     *
     * @param cp CardPack to generate Markdown for
     * @return A Markdown String
     */
    private String generateCardPackMarkdown(final CardPack cp) {
        final StringBuilder sb = new StringBuilder();
        final int blackCards = cp.getBlackCards().size();
        final int whiteCards = cp.getWhiteCards().size();
        final int totalCards = blackCards + whiteCards;
        sb.append("## ").append(cp.getName()).append("\n");
        final String description = cp.getDescription();
        final String author = cp.getAuthor();
        if (description != null || author != null) {
            sb.append("### Metadata\n");
            sb.append("|Title|Data|\n|---|---|\n");
            if (description != null) {
                sb.append("|Description|").append(description).append("|\n");
            }
            if (author != null) {
                sb.append("|Author|").append(author).append("|\n");
            }
        }
        sb.append("### Information\n");
        sb.append("|Title|Data|\n|---|---|\n");
        sb.append("|Total cards|").append(totalCards).append("|\n");
        sb.append("|Black cards|").append(blackCards).append(" (").append(PacksCommand.df.format((double) blackCards / (double) totalCards)).append(")|\n");
        sb.append("|White cards|").append(whiteCards).append(" (").append(PacksCommand.df.format((double) whiteCards / (double) totalCards)).append(")|\n");
        sb.append("|Random black card|").append("```").append(cp.getBlackCards().get(PacksCommand.r.nextInt(blackCards))).append("```|\n");
        sb.append("|Random white card|").append("```").append(cp.getWhiteCards().get(PacksCommand.r.nextInt(whiteCards))).append("```|\n");
        return sb.toString();
    }

    @Override
    public void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        final StringBuilder sb = new StringBuilder();
        final String allPackNames = this.humanity.getLoadedCardPacks().stream().map(CardPack::getName).sorted().collect(Collectors.joining());
        sb.append("# All card packs\n");
        sb.append("- ").append(
            this.humanity.getLoadedCardPacks().stream().map(CardPack::getName).collect(Collectors.joining("\n- "))
        );
        sb.append("\n\n# Individual card packs\n");
        for (final CardPack cp : this.humanity.getLoadedCardPacks()) {
            sb.append(this.generateCardPackMarkdown(cp));
        }
        ConversionHelper.respond(event, this.humanity.cachedGist(PacksCommand.GIST_ID, allPackNames, "packs.md", sb.toString()));
    }
}
