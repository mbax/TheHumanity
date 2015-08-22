package org.royaldev.thehumanity.cards.play;

import org.royaldev.thehumanity.util.json.JSONSerializable;

import java.io.Serializable;
import java.util.List;

public class PlaySnapshot implements Serializable, JSONSerializable {

    private static final long serialVersionUID = 44L;
    private String player;
    private List<String> whiteCards;
    private int[] handIndices;

    public PlaySnapshot() {}

    public PlaySnapshot(final String player, final int[] indices, final List<String> whiteCards) {
        this.player = player;
        this.handIndices = indices;
        this.whiteCards = whiteCards;
    }

    public int[] getHandIndices() {
        return this.handIndices;
    }

    public String getPlayer() {
        return this.player;
    }

    public List<String> getWhiteCards() {
        return this.whiteCards;
    }
}
