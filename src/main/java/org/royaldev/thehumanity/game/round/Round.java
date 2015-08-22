package org.royaldev.thehumanity.game.round;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.royaldev.thehumanity.cards.play.Play;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.player.Player;

import java.util.List;
import java.util.Set;

public interface Round {

    @NotNull
    BlackCard getBlackCard();

    @Nullable
    Player getCzar();

    Play getMostVoted();

    int getNumber();

    @NotNull
    List<Play> getPlays();

    @NotNull
    Set<Player> getSkippedPlayers();

    enum RoundStage {
        /**
         * This round has not started.
         */
        IDLE,
        /**
         * The players are submitting their cards for this round.
         */
        WAITING_FOR_PLAYERS,
        /**
         * The czar is choosing a winner for this round.
         */
        WAITING_FOR_CZAR,
        /**
         * This round has ended.
         */
        ENDED
    }

    enum RoundEndCause {
        NOT_ENDED,
        CZAR_CHOSE_WINNER,
        CZAR_LEFT,
        CZAR_SKIPPED,
        GAME_ENDED
    }
}
