package org.royaldev.thehumanity.cards.types;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.royaldev.thehumanity.cards.Card;
import org.royaldev.thehumanity.cards.packs.CardPack;

/**
 * A white card.
 */
public class WhiteCard extends Card {

    /**
     * Constructs a white card for the given pack with the given text.
     *
     * @param cardPack Pack the white card belongs to
     * @param rawText  Text of the white card
     */
    public WhiteCard(final CardPack cardPack, final String rawText) {
        super(cardPack, rawText);
    }

    /**
     * Processes text for public viewing.
     *
     * @param rawText Internal text
     * @return Public-viewable text
     */
    @NotNull
    @Override
    protected String processText(@NotNull final String rawText) {
        Preconditions.checkNotNull(rawText, "rawText was null");
        return rawText.replace("\\#", "#");
    }
}
