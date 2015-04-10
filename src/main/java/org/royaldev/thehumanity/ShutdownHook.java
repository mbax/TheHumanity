package org.royaldev.thehumanity;

import org.royaldev.thehumanity.game.Game.GameEndCause;

public class ShutdownHook implements Runnable {

    private final TheHumanity humanity;

    public ShutdownHook(final TheHumanity humanity) {
        this.humanity = humanity;
    }

    @Override
    public void run() {
        this.humanity.getGames().values().forEach(game -> game.stop(GameEndCause.JAVA_SHUTDOWN));
        this.humanity.getBot().shutdown("TooManyCardsException (See you!)");
        this.humanity.getPingRegistry().save();
    }
}
