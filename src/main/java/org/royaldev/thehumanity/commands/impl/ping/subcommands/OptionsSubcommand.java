package org.royaldev.thehumanity.commands.impl.ping.subcommands;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.impl.ping.PingListSubcommand;
import org.royaldev.thehumanity.ping.PingRegistration;
import org.royaldev.thehumanity.ping.PingRegistrationOption;
import org.royaldev.thehumanity.ping.PingRegistry;
import org.royaldev.thehumanity.ping.WhoX;

import java.util.Arrays;
import java.util.List;

@Command(
    name = "options",
    description = "Sets options for your registration.",
    aliases = {"o", "option", "config", "configuration"}
)
public class OptionsSubcommand extends PingListSubcommand {

    private final TheHumanity humanity;

    public OptionsSubcommand(final TheHumanity humanity) {
        this.humanity = humanity;
    }

    private void add(@NotNull final PingRegistry pr, @NotNull final User u, @NotNull final WhoX whoX, @NotNull final String accountName, @NotNull final String[] args) {
        Preconditions.checkNotNull(pr, "pr was null");
        Preconditions.checkNotNull(u, "u was null");
        Preconditions.checkNotNull(whoX, "whoX was null");
        Preconditions.checkNotNull(accountName, "accountName was null");
        Preconditions.checkNotNull(args, "args was null");
        if (args.length < 1) {
            this.notice(u, "You must provide an option to add.");
            return;
        }
        final PingRegistrationOption pro;
        try {
            pro = PingRegistrationOption.valueOf(args[0].toUpperCase());
        } catch (final IllegalArgumentException ex) {
            this.notice(u, "Invalid option.");
            return;
        }
        final PingRegistration registration = pr.getRegistration(accountName);
        if (registration == null) {
            this.notice(u, "You don't have a registration.");
            return;
        }
        if (registration.hasOption(pro)) {
            this.notice(u, "That option is already enabled.");
            return;
        }
        this.notice(u, "Option " + (registration.addOption(pro) ? "" : "could not be ") + "added.");
    }

    private void list(@NotNull final PingRegistry pr, @NotNull final User u, @NotNull final WhoX whoX, @NotNull final String accountName) {
        Preconditions.checkNotNull(pr, "pr was null");
        Preconditions.checkNotNull(u, "u was null");
        Preconditions.checkNotNull(whoX, "whoX was null");
        Preconditions.checkNotNull(accountName, "accountName was null");
        final List<String> messages = Lists.newArrayList();
        final PingRegistration registration = pr.getRegistration(accountName);
        if (registration == null) {
            this.notice(u, "You don't have a registration.");
            return;
        }
        for (final PingRegistrationOption pro : PingRegistrationOption.values()) {
            messages.add(
                String.format(
                    "%s%s%s (%s)",
                    registration.hasOption(pro) ? IRCFormat.GREEN : IRCFormat.RED,
                    pro.name(),
                    IRCFormat.RESET,
                    pro.getDescription()
                )
            );
        }
        messages.forEach(message -> this.notice(u, message));
    }

    private void remove(@NotNull final PingRegistry pr, @NotNull final User u, @NotNull final WhoX whoX, @NotNull final String accountName, @NotNull final String[] args) {
        Preconditions.checkNotNull(pr, "pr was null");
        Preconditions.checkNotNull(u, "u was null");
        Preconditions.checkNotNull(whoX, "whoX was null");
        Preconditions.checkNotNull(accountName, "accountName was null");
        Preconditions.checkNotNull(args, "args was null");
        if (args.length < 1) {
            this.notice(u, "You must provide an option to remove.");
            return;
        }
        final PingRegistrationOption pro;
        try {
            pro = PingRegistrationOption.valueOf(args[0].toUpperCase());
        } catch (final IllegalArgumentException ex) {
            this.notice(u, "Invalid option.");
            return;
        }
        final PingRegistration registration = pr.getRegistration(accountName);
        if (registration == null) {
            this.notice(u, "You don't have a registration.");
            return;
        }
        if (!registration.hasOption(pro)) {
            this.notice(u, "That option is not enabled.");
            return;
        }
        this.notice(u, "Option " + (registration.removeOption(pro) ? "" : "could not be ") + "removed.");
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
        final PingRegistry pr = this.humanity.getPingRegistry();
        if (!pr.hasRegistration(accountName)) {
            this.notice(u, "You don't have a registration.");
            return;
        }
        if (args.length < 1) {
            this.notice(u, "You must provide a subcommand.");
            this.notice(u, "Subcommands: (list, l), (add, a), (remove, r)");
            return;
        }
        final String subcommand = args[0].toLowerCase();
        final String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        switch (subcommand) {
            case "list":
            case "l":
                this.list(pr, u, whoX, accountName);
                break;
            case "add":
            case "a":
                this.add(pr, u, whoX, accountName, newArgs);
                break;
            case "remove":
            case "r":
                this.remove(pr, u, whoX, accountName, newArgs);
                break;
            default:
                this.notice(u, "Unknown subcommand.");
                break;
        }
    }
}
