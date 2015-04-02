package org.royaldev.thehumanity;

public class ShutdownHook implements Runnable {

    private final TheHumanity humanity;

    public ShutdownHook(final TheHumanity humanity) {
        this.humanity = humanity;
    }

    @Override
    public void run() {
        this.humanity.getBot().shutdown("TooManyCardsException (See you!)");
        this.humanity.getPingRegistry().save();
    }
}
