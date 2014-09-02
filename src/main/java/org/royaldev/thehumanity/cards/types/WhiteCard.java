package org.royaldev.thehumanity.cards.types;

import org.royaldev.thehumanity.cards.Card;
import org.royaldev.thehumanity.cards.CardPack;

public class WhiteCard extends Card {

    public WhiteCard(final CardPack cardPack, final String rawText) {
        super(cardPack, rawText);
    }

    @Override
    protected String processText(final String rawText) {
        return rawText.replace("\\#", "#");
    }
}
