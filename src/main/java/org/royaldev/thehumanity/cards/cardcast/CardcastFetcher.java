package org.royaldev.thehumanity.cards.cardcast;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * Constructs a new fether for the given Cardcast ID.
     *
     * @param id ID of the Cardcast pack
     */
    public CardcastFetcher(final String id) {
        this.id = id;
        this.getInfo();
    }

    /**
     * Gets the text of a black card, given its parts.
     *
     * @param parts Parts of the black card
     * @return Complete text
     */
    private String getBlackCardText(final JSONArray parts) {
        final List<String> listParts = new ArrayList<>();
        for (int i = 0; i < parts.length(); i++) {
            listParts.add(parts.getString(i));
        }
        return StringUtils.join(listParts, "_");
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
    public List<Card> getBlackCards(final CardPack cp, final JSONArray calls) {
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
        } catch (UnirestException e) {
            e.printStackTrace();
            return;
        }
        this.name = hr.getBody().getObject().getString("name");
    }

    /**
     * Gets all the white cards of the Cardcast pack.
     *
     * @param cp        CardPack for white cards
     * @param responses Cardcast white cards
     * @return All converted white cards
     */
    public List<Card> getWhiteCards(final CardPack cp, final JSONArray responses) {
        final List<Card> whiteCards = new ArrayList<>();
        for (int i = 0; i < responses.length(); i++) {
            final JSONObject response = responses.getJSONObject(i);
            whiteCards.add(new WhiteCard(cp, response.getJSONArray("text").getString(0).replaceAll("\\.$", "")));
        }
        return whiteCards;
    }

}
