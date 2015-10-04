package org.royaldev.thehumanity.commands;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.helper.ActorEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ParentCommand extends NoticeableCommand {

    private final List<IRCCommand> subcommands = new ArrayList<>();

    protected void addSubcommand(@NotNull final IRCCommand subcommand) {
        Preconditions.checkNotNull(subcommand, "subcommand was null");
        this.subcommands.add(subcommand);
    }

    protected boolean beforeCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        return true;
    }

    protected boolean beforeSubcommand(@NotNull final IRCCommand subcommand, @NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        return true;
    }

    @NotNull
    protected String getHelpString() {
        final StringBuilder sb = new StringBuilder("Subcommands: ");
        for (final IRCCommand subcommand : this.subcommands) {
            sb.append("(").append(subcommand.getName());
            for (final String alias : subcommand.getAliases()) {
                sb.append(", ").append(alias);
            }
            sb.append("), ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    @Nullable
    protected IRCCommand getSubcommand(@NotNull final String name) {
        Preconditions.checkNotNull(name, "name was null");
        return this.subcommands.stream()
            .filter(c -> c.getName().equalsIgnoreCase(name) || Arrays.asList(c.getAliases()).contains(name.toLowerCase()))
            .findFirst()
            .orElse(null);
    }

    @Override
    public void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args) {
        if (!this.beforeCommand(event, ci, args)) {
            return;
        }
        final User u = event.getActor();
        if (args.length < 1) {
            this.notice(u, "Provide a subcommand.");
            this.notice(u, this.getHelpString());
            return;
        }
        final IRCCommand subcommand = this.getSubcommand(args[0]);
        if (subcommand == null) {
            this.notice(u, "No such subcommand.");
            return;
        }
        if (!this.beforeSubcommand(subcommand, event, ci, args)) {
            return;
        }
        subcommand.onCommand(event, ci, Arrays.copyOfRange(args, 1, args.length));
    }
}
