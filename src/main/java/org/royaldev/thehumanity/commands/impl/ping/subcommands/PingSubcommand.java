package org.royaldev.thehumanity.commands.impl.ping.subcommands;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.impl.ping.PingListSubcommand;
import org.royaldev.thehumanity.ping.PingRegistry;
import org.royaldev.thehumanity.ping.WhoX;

import java.util.concurrent.TimeUnit;

@Command(
    name = "ping",
    description = "Sends a notice to all who are configured to receive notices.",
    aliases = {"p", "notice", "notify"}
)
public class PingSubcommand extends PingListSubcommand {

    private static long lastUsed = 0L;
    private final TheHumanity humanity;

    public PingSubcommand(final TheHumanity humanity) {
        this.humanity = humanity;
    }

    private boolean hasBeenTenMinutes() {
        return TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - PingSubcommand.lastUsed) >= 10L;
    }

    private String timeToWait() {
        final long nanoSeconds = TimeUnit.MINUTES.toNanos(10L) - (System.nanoTime() - PingSubcommand.lastUsed);
        final long minutes = TimeUnit.NANOSECONDS.toMinutes(nanoSeconds);
        final long seconds = TimeUnit.NANOSECONDS.toSeconds(nanoSeconds) % 60L;
        return String.format(
            "%s minute%s and %s second%s",
            minutes,
            minutes == 1L ? "" : "s",
            seconds,
            seconds == 1L ? "" : "s"
        );
    }

    private void updateLastUsedTime() {
        PingSubcommand.lastUsed = System.nanoTime();
    }

    @Override
    public void onSubcommand(@NotNull final ChannelMessageEvent event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        final User u = event.getActor();
        final WhoX whoX = this.humanity.getWhoX();
        final String accountName = whoX.getAccount(u.getMessagingName());
        if (accountName == null) {
            this.notice(u, "You must be registered with services to use this command.");
            return;
        }
        if (!this.hasBeenTenMinutes()) {
            this.notice(u, "You must wait " + this.timeToWait() + " before this command can be used again.");
            return;
        }
        final PingRegistry pr = this.humanity.getPingRegistry();
        this.updateLastUsedTime();
        pr.sendNotifications(u, event.getChannel(), this.humanity.getBot(), whoX);
        this.notice(u, "Notices sent.");
    }
}
