package org.royaldev.thehumanity.server.services.cardpack;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.packs.CardPack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HumanityCardPackService implements CardPackService {

    @Autowired
    private TheHumanity humanity;

    @Override
    public List<CardPack> getAll() {
        return this.humanity.getLoadedCardPacks();
    }

    @Override
    public CardPack getFromName(@NotNull final String name) {
        Preconditions.checkNotNull(name, "name was null");
        return this.humanity.getCardPack(name);
    }
}
