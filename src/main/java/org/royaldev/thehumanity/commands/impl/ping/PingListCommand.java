package org.royaldev.thehumanity.commands.impl.ping;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.helper.ActorEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.IRCCommand;
import org.royaldev.thehumanity.commands.ParentCommand;
import org.royaldev.thehumanity.commands.impl.ping.subcommands.AddSubcommand;
import org.royaldev.thehumanity.commands.impl.ping.subcommands.OptionsSubcommand;
import org.royaldev.thehumanity.commands.impl.ping.subcommands.PingSubcommand;
import org.royaldev.thehumanity.commands.impl.ping.subcommands.RemoveSubcommand;
import org.royaldev.thehumanity.ping.WhoX;

import java.util.Arrays;

@Command(
    name = "pinglist",
    description = "Manages the ping registry.",
    aliases = {"pingregistry", "pl", "pr"},
    usage = "<command> [subcommand]"
)
public class PingListCommand extends ParentCommand {

    private final TheHumanity humanity;

    public PingListCommand(final TheHumanity instance) {
        this.humanity = instance;
        Arrays.asList(
            new AddSubcommand(this.humanity),
            new PingSubcommand(this.humanity),
            new RemoveSubcommand(this.humanity),
            new OptionsSubcommand(this.humanity)
        ).forEach(this::addSubcommand);
    }

    /**
     * Waits for a WhoX to finish in a new thread, then sends a WhoX to the server. This takes a user and a channel. The
     * user is the person using the command and cannot be null. The channel, however, may be null if the user is in a
     * private message. If the channel is null, the WhoX will only be sent for the one user.
     * <p>If the WhoX contains an account mapping for the user after completing, onCommand will be called again. If not,
     * a notice will be sent to the user.
     *
     * @param whoX    WhoX instance
     * @param user    User using command
     * @param channel Channel the command is being used in or null
     * @param event   Event this is in response to
     * @param ci      CallInfo
     * @param args    Arguments
     */
    private void updateWhoX(@NotNull WhoX whoX, @NotNull final User user, @Nullable final Channel channel, @NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        Preconditions.checkNotNull(whoX, "whoX was null");
        Preconditions.checkNotNull(user, "user was null");
        Preconditions.checkNotNull(event, "event was null");
        Preconditions.checkNotNull(ci, "ci was null");
        Preconditions.checkNotNull(args, "args was null");
        new Thread(() -> {
            synchronized (whoX.getFinishObject()) {
                try {
                    whoX.getFinishObject().wait();
                } catch (final InterruptedException ex) {
                    this.notice(user, "Interrupted while waiting: " + ex.getMessage());
                    return;
                }
            }
            if (whoX.hasAccountMapping(user.getMessagingName())) {
                this.onCommand(event, ci, args);
            } else {
                this.notice(user, "Couldn't get an account for you.");
            }
        }).start();
        if (channel == null) {
            whoX.sendWhoX(user);
        } else {
            whoX.sendWhoX(channel);
        }
    }

    /**
     * Sends a WhoX for a whole channel.
     *
     * @param whoX  WhoX instance
     * @param event ChannelMessageEvent this is in response to
     * @param ci    CallInfo
     * @param args  Arguments
     */
    private void updateWhoX(@NotNull WhoX whoX, @NotNull final ChannelMessageEvent event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        this.updateWhoX(whoX, event.getActor(), event.getChannel(), event, ci, args);
    }

    @Override
    public boolean beforeCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        final User u = event.getActor();
        if (!(event instanceof ChannelMessageEvent)) {
            this.notice(u, "This command must be used in a channel.");
            return false;
        }
        final WhoX whoX = this.humanity.getWhoX();
        if (!whoX.hasAccountMapping(u.getMessagingName())) {
            this.updateWhoX(whoX, (ChannelMessageEvent) event, ci, args);
            return false;
        }
        return true;
    }

    @Override
    protected boolean beforeSubcommand(@NotNull final IRCCommand subcommand, @NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        final User u = event.getActor();
        final WhoX whoX = this.humanity.getWhoX();
        final String accountName = whoX.getAccount(u.getMessagingName());
        if (accountName == null) {
            this.notice(u, "You must be registered with services to use the ping list.");
            return false;
        }
        return true;
    }
}
