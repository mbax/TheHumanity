package org.royaldev.thehumanity.cards.packs;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public class CardcastCardPack extends MemoryCardPack {

    private final String id;

    public CardcastCardPack(@NotNull final String name, @NotNull final String id) {
        super(name);
        Preconditions.checkNotNull(id, "id was null");
        this.id = id;
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof CardcastCardPack && ((CardcastCardPack) obj).getID().equals(this.getID());
    }

    @NotNull
    public String getID() {
        return this.id;
    }
}
