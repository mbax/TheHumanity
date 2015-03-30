package org.royaldev.thehumanity;

import org.royaldev.thehumanity.cards.packs.CardPack;
import org.royaldev.thehumanity.cards.Deck;
import org.royaldev.thehumanity.cards.packs.MemoryCardPack;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.powermock.api.mockito.PowerMockito.mock;

public final class CardHelper {

    private static final CardPack cp = mock(MemoryCardPack.class);

    public static BlackCard makeBlackCard(final CardPack cp, final String text) {
        return new BlackCard(cp, text);
    }

    public static BlackCard makeBlackCard(final String text) {
        return CardHelper.makeBlackCard(CardHelper.cp, text);
    }

    public static List<BlackCard> makeBlackCards(final CardPack cp, final String... texts) {
        return Arrays.stream(texts).map(text -> CardHelper.makeBlackCard(cp, text)).collect(Collectors.toList());
    }

    public static List<BlackCard> makeBlackCards(final String... texts) {
        return CardHelper.makeBlackCards(CardHelper.cp, texts);
    }

    public static CardPack makeCardPack(final String name, final List<String> whiteCards, final List<String> blackCards) {
        final CardPack cp = new MemoryCardPack(name);
        whiteCards.stream().map(text -> CardHelper.makeWhiteCard(cp, text)).forEach(cp::addCard);
        blackCards.stream().map(text -> CardHelper.makeBlackCard(cp, text)).forEach(cp::addCard);
        return cp;
    }

    public static Deck makeDeck(final List<CardPack> cardPacks) {
        return new Deck(cardPacks);
    }

    public static WhiteCard makeWhiteCard(final String text) {
        return CardHelper.makeWhiteCard(CardHelper.cp, text);
    }

    public static WhiteCard makeWhiteCard(final CardPack cp, final String text) {
        return new WhiteCard(cp, text);
    }

    public static List<WhiteCard> makeWhiteCards(final String... texts) {
        return CardHelper.makeWhiteCards(CardHelper.cp, texts);
    }

    public static List<WhiteCard> makeWhiteCards(final CardPack cp, final String... texts) {
        return Arrays.stream(texts).map(text -> CardHelper.makeWhiteCard(cp, text)).collect(Collectors.toList());
    }

}
