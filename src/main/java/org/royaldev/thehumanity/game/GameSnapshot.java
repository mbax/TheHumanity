package org.royaldev.thehumanity.game;

import org.royaldev.thehumanity.util.json.JSONSerializable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GameSnapshot implements Serializable, JSONSerializable {

    private static final long serialVersionUID = 45L;
    private List<String> players, historicPlayers, houseRules, cardPacks;
    private Map<String, Integer> scores;
    private String channel, endCause, host;
    private Timestamps timestamps;
    private int rounds;

    public GameSnapshot() {}

    public GameSnapshot(final String channel, final String endCause, final long startTime, final long endTime, final List<String> players, final List<String> historicPlayers, final List<String> houseRules, final List<String> cardPacks, final Map<String, Integer> scores, final String host, final int rounds) {
        this.players = players;
        this.historicPlayers = historicPlayers;
        this.houseRules = houseRules;
        this.cardPacks = cardPacks;
        this.channel = channel;
        this.endCause = endCause;
        this.host = host;
        this.timestamps = new Timestamps(startTime, endTime);
        this.scores = scores;
        this.rounds = rounds;
    }

    public List<String> getCardPacks() {
        return this.cardPacks;
    }

    public String getChannel() {
        return this.channel;
    }

    public String getEndCause() {
        return this.endCause;
    }

    public List<String> getHistoricPlayers() {
        return this.historicPlayers;
    }

    public String getHost() {
        return this.host;
    }

    public List<String> getHouseRules() {
        return this.houseRules;
    }

    public List<String> getPlayers() {
        return this.players;
    }

    public int getRounds() {
        return this.rounds;
    }

    public Map<String, Integer> getScores() {
        return this.scores;
    }

    public Timestamps getTimestamps() {
        return timestamps;
    }

    public static class Timestamps {

        private long started, ended;

        public Timestamps() {}

        public Timestamps(final long started, final long ended) {
            this.started = started;
            this.ended = ended;
        }

        public long getEnded() {
            return this.ended;
        }

        public long getStarted() {
            return this.started;
        }
    }
}
