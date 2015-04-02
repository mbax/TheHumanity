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

@Command(
    name = "remove",
    description = "Removes yourself from the registry.",
    aliases = {"r", "unregister"}
)
public class RemoveSubcommand extends PingListSubcommand {

    private final TheHumanity humanity;

    public RemoveSubcommand(final TheHumanity humanity) {
        this.humanity = humanity;
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
        final boolean success = pr.removeRegistration(accountName);
        this.notice(u, "Registration " + (success ? "" : "could not be ") + "removed for services account " + accountName + ".");
    }
}
