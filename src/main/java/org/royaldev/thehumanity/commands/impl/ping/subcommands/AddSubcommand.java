package org.royaldev.thehumanity.commands.impl.ping.subcommands;

import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.Command;
import org.royaldev.thehumanity.commands.impl.ping.PingListSubcommand;
import org.royaldev.thehumanity.ping.PingRegistration;
import org.royaldev.thehumanity.ping.PingRegistry;
import org.royaldev.thehumanity.ping.WhoX;

@Command(
    name = "add",
    description = "Adds yourself to the registry.",
    aliases = {"a", "register"}
)
public class AddSubcommand extends PingListSubcommand {

    private final TheHumanity humanity;

    public AddSubcommand(final TheHumanity humanity) {
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
        if (pr.hasRegistration(accountName)) {
            this.notice(u, "You already have a registration.");
            return;
        }
        final PingRegistration registration = pr.addRegistration(accountName);
        this.notice(u, "Registration added for services account " + registration.getServicesAccount() + ".");
    }
}
