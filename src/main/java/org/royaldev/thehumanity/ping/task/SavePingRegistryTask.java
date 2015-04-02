package org.royaldev.thehumanity.ping.task;

import org.royaldev.thehumanity.ping.PingRegistry;

public class SavePingRegistryTask implements Runnable {

    private final PingRegistry pingRegistry;

    public SavePingRegistryTask(final PingRegistry pingRegistry) {
        this.pingRegistry = pingRegistry;
    }

    @Override
    public void run() {
        try {
            this.pingRegistry.save();
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }
    }
}
