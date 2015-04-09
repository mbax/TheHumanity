package org.royaldev.thehumanity.cards.play;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.IRCFormat;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.player.Player;
import org.royaldev.thehumanity.util.Snapshottable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A play. A play contains all the {@link org.royaldev.thehumanity.cards.types.WhiteCard WhiteCards} that a player used
 * to answer a {@link org.royaldev.thehumanity.cards.types.BlackCard BlackCard}.
 */
public class Play implements Snapshottable<PlaySnapshot> {

    private final Player player;
    private final List<WhiteCard> whiteCards = new ArrayList<>();
    private final int[] handIndices;

    /**
     * Constructs a new play for the given player, using the given cards.
     *
     * @param player     Player that played the cards
     * @param whiteCards Cards the player played
     */
    public Play(final Player player, final List<WhiteCard> whiteCards) {
        this.player = player;
        this.whiteCards.addAll(whiteCards);
        this.handIndices = whiteCards.stream().mapToInt(wc -> this.player.getHand().getCards().indexOf(wc) + 1).toArray();
    }

    public Play(final Player player, final List<WhiteCard> whiteCards, final int[] handIndices) {
        this.player = player;
        this.whiteCards.addAll(whiteCards);
        this.handIndices = handIndices;
    }

    public int[] getHandIndices() {
        return this.handIndices;
    }

    /**
     * Gets the player that made this play.
     *
     * @return Player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the white cards used in this play.
     *
     * @return White cards
     */
    public List<WhiteCard> getWhiteCards() {
        return this.whiteCards;
    }

    @NotNull
    @Override
    public PlaySnapshot takeSnapshot() {
        return new PlaySnapshot(
            this.player.getUser().getNick(),
            this.handIndices,
            this.getWhiteCards().stream().map(WhiteCard::getText).collect(Collectors.toList())
        );
    }

    /**
     * Returns a comma-delimited list of the white cards in this play, using their
     * {@link org.royaldev.thehumanity.cards.types.WhiteCard@getText getText()} method.
     * <p/>
     * <strong>Note</strong> that IRC colors are included in this representation.
     *
     * @return Comma-delimited String
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final WhiteCard wc : this.getWhiteCards()) {
            sb.append(IRCFormat.BOLD).append(wc.getText()).append(IRCFormat.RESET).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}
