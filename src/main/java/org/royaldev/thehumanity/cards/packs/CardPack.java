package org.royaldev.thehumanity.cards.packs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.royaldev.thehumanity.cards.Card;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;

import java.util.List;

public interface CardPack {

    void addCard(@NotNull Card c);

    @Nullable
    String getAuthor();

    @NotNull
    List<BlackCard> getBlackCards();

    @Nullable
    String getDescription();

    @NotNull
    String getName();

    @NotNull
    List<WhiteCard> getWhiteCards();

    void setAuthor(@Nullable String author);

    void setDescription(@Nullable String description);
}
