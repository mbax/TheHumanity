package org.royaldev.thehumanity.server.services.cardpack;

import org.jetbrains.annotations.NotNull;
import org.royaldev.thehumanity.cards.packs.CardPack;

import java.util.List;

public interface CardPackService {

    List<CardPack> getAll();

    CardPack getFromName(@NotNull final String name);

}
