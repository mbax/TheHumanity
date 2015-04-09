package org.royaldev.thehumanity.game.round;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.royaldev.thehumanity.cards.play.PlaySnapshot;
import org.royaldev.thehumanity.game.GameSnapshot.Timestamps;
import org.royaldev.thehumanity.util.json.JSONSerializable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A historical snapshot of a round. It has no reference to its parent game, but historical games should have reference
 * to their historical rounds.
 */
public class RoundSnapshot implements Serializable, JSONSerializable {

    private static final long serialVersionUID = 43L;
    private int number;
    private Timestamps timestamps;
    private String blackCard, czar, winner, endCause;
    private Map<String, List<IndividualPlaySnapshot>> plays;
    private Set<String> players, skippedPlayers;
    private Map<String, Integer> scoreDelta, votes;

    public RoundSnapshot() {}

    public RoundSnapshot(final int number, final long startTime, final long endTime, final String blackCard, final String czar, final String winner, final String endCause, final List<PlaySnapshot> plays, final Set<String> players, final Set<String> skippedPlayers, final Map<String, Integer> scoreDelta, final Map<String, Integer> votes) {
        this.number = number;
        this.timestamps = new Timestamps(startTime, endTime);
        this.blackCard = blackCard;
        this.czar = czar;
        this.winner = winner;
        this.endCause = endCause;
        this.plays = plays.stream().collect(Collectors.toMap(
            PlaySnapshot::getPlayer,
            ps -> ps.getWhiteCards().stream()
                .map(
                    wc -> new IndividualPlaySnapshot(
                        wc,
                        ps.getHandIndices()[ps.getWhiteCards().indexOf(wc)]
                    )
                )
                .collect(Collectors.toList())
        ));
        this.players = players;
        this.skippedPlayers = skippedPlayers;
        this.scoreDelta = scoreDelta;
        this.votes = votes;
    }

    /**
     * Gets the black card for this round.
     *
     * @return Black card
     */
    @NotNull
    public String getBlackCard() {
        return this.blackCard;
    }

    /**
     * Gets the czar for this round. This may be null if and only if a house rule such as God is Dead, which removes any
     * singular czar and enables voting, is being used.
     *
     * @return Czar's nickname or null
     */
    @Nullable
    public String getCzar() {
        return this.czar;
    }

    public String getEndCause() {
        return this.endCause;
    }

    /**
     * Gets the round number, starting at 1.
     *
     * @return Round number
     */
    public int getNumber() {
        return this.number;
    }

    public Set<String> getPlayers() {
        return players;
    }

    /**
     * Gets the plays at this point in time.
     *
     * @return List of plays (snapshotted)
     */
    @NotNull
    public Map<String, List<IndividualPlaySnapshot>> getPlays() {
        return this.plays;
    }

    public Map<String, Integer> getScoreDelta() {
        return scoreDelta;
    }

    /**
     * Gets any skipped players' nicknames.
     *
     * @return List of nicknames
     */
    @NotNull
    public Set<String> getSkippedPlayers() {
        return this.skippedPlayers;
    }

    public Timestamps getTimestamps() {
        return this.timestamps;
    }

    public Map<String, Integer> getVotes() {
        return votes;
    }

    /**
     * Gets the winner at this point in time. May be null if this snapshot is from before a winner was declared or if
     * the round this snapshot represents was ended before a winner could be decided.
     *
     * @return Winner's nickname or null
     */
    @Nullable
    public String getWinner() {
        return this.winner;
    }

    public static class IndividualPlaySnapshot implements Serializable, JSONSerializable {

        private String text;
        private int handIndex;

        public IndividualPlaySnapshot() {}

        public IndividualPlaySnapshot(final String text, final int handIndex) {
            this.text = text;
            this.handIndex = handIndex;
        }

        public int getHandIndex() {
            return this.handIndex;
        }

        public String getText() {
            return this.text;
        }
    }
}
