package org.royaldev.thehumanity.cards.cardcast;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.royaldev.thehumanity.cards.Card;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Class the creates CardPacks based on Cardcast IDs.
 */
public class CardcastFetcher {

    private static final String INFO_URL = "https://api.cardcastgame.com/v1/decks/%s";
    private static final String CARDS_URL = CardcastFetcher.INFO_URL + "/cards";
    private final String id;
    private String name;
    private String description;
    private String author;

    /**
     * Constructs a new fether for the given Cardcast ID.
     *
     * @param id ID of the Cardcast pack
     */
    public CardcastFetcher(@NotNull final String id) {
        Preconditions.checkNotNull(id, "id was null");
        this.id = id;
        this.getInfo();
    }

    /**
     * Gets the text of a black card, given its parts.
     *
     * @param parts Parts of the black card
     * @return Complete text
     */
    @NotNull
    private String getBlackCardText(@NotNull final JSONArray parts) {
        Preconditions.checkNotNull(parts, "parts was null");
        final List<String> listParts = new ArrayList<>();
        for (int i = 0; i < parts.length(); i++) {
            listParts.add(parts.getString(i));
        }
        return Joiner.on('_').join(listParts);
    }

    /**
     * Adds the list of cards to the CardPack.
     *
     * @param cp    CardPack to add to
     * @param cards Cards to add
     */
    public void addCards(final CardPack cp, final List<Card> cards) {
        cards.forEach(cp::addCard);
    }

    /**
     * Gets all the black cards of the Cardcast pack.
     *
     * @param cp    CardPack for black cards
     * @param calls Cardcast black cards
     * @return All converted black cards
     */
    @NotNull
    public List<Card> getBlackCards(@NotNull final CardPack cp, @NotNull final JSONArray calls) {
        Preconditions.checkNotNull(cp, "cp was null");
        Preconditions.checkNotNull(calls, "calls was null");
        final List<Card> blackCards = new ArrayList<>();
        for (int i = 0; i < calls.length(); i++) {
            final JSONObject call = calls.getJSONObject(i);
            blackCards.add(new BlackCard(cp, this.getBlackCardText(call.getJSONArray("text"))));
        }
        return blackCards;
    }

    /**
     * Gets the converted CardPack. If there was an error contacting Cardcast, this returns null.
     *
     * @return CardPack or null
     */
    @Nullable
    public CardPack getCardPack() {
        final HttpResponse<JsonNode> hr;
        try {
            hr = Unirest.get(String.format(CardcastFetcher.CARDS_URL, this.id)).asJson();
        } catch (final UnirestException e) {
            e.printStackTrace();
            return null;
        }
        final JSONObject root = hr.getBody().getObject();
        final CardPack cp = new CardPack(this.name);
        cp.setDescription(this.description);
        cp.setAuthor(this.author);
        this.addCards(cp, this.getWhiteCards(cp, root.getJSONArray("responses")));
        this.addCards(cp, this.getBlackCards(cp, root.getJSONArray("calls")));
        return cp;
    }

    /**
     * Sets the information about this CardPack from Cardcast's API.
     */
    public void getInfo() {
        final HttpResponse<JsonNode> hr;
        try {
            hr = Unirest.get(String.format(CardcastFetcher.INFO_URL, this.id)).asJson();
        } catch (final UnirestException ex) {
            ex.printStackTrace();
            return;
        }
        final JSONObject info = hr.getBody().getObject();
        this.name = info.optString("name", this.id);
        this.description = info.optString("description");
        final JSONObject authorInfo = info.optJSONObject("author");
        if (authorInfo != null) {
            this.author = authorInfo.optString("username");
        }
    }

    /**
     * Gets all the white cards of the Cardcast pack.
     *
     * @param cp        CardPack for white cards
     * @param responses Cardcast white cards
     * @return All converted white cards
     */
    @NotNull
    public List<Card> getWhiteCards(@NotNull final CardPack cp, @NotNull final JSONArray responses) {
        Preconditions.checkNotNull(cp, "cp was null");
        Preconditions.checkNotNull(responses, "responses was null");
        final List<Card> whiteCards = new ArrayList<>();
        for (int i = 0; i < responses.length(); i++) {
            final JSONObject response = responses.getJSONObject(i);
            whiteCards.add(new WhiteCard(cp, response.getJSONArray("text").getString(0).replaceAll("\\.$", "")));
        }
        return whiteCards;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .omitNullValues()
            .add("id", this.id)
            .add("name", this.name)
            .add("description", this.description)
            .add("author", this.author)
            .toString();
    }

}
