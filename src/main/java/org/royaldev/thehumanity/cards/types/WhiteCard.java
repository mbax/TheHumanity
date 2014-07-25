package org.royaldev.thehumanity.cards.types;

import org.royaldev.thehumanity.cards.Card;
import org.royaldev.thehumanity.cards.CardPack;

public class WhiteCard extends Card {

    public WhiteCard(CardPack cardPack, String rawText) {
        super(cardPack, rawText);
    }

    @Override
    protected String processText(String rawText) {
        return rawText;
    }
}
