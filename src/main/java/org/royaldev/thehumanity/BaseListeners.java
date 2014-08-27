package org.royaldev.thehumanity;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.IRCCommand;

/**
 * The basic listeners of the bot that allow it to function.
 */
final class BaseListeners extends ListenerAdapter<PircBotX> {

    private final TheHumanity humanity;

    BaseListeners(TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public void onConnect(ConnectEvent<PircBotX> event) throws Exception {
        event.getBot().sendIRC().mode(event.getBot().getNick(), "+B");
    }

    @Override
    public void onInvite(InviteEvent e) {
        e.getBot().sendIRC().joinChannel(e.getChannel());
        this.humanity.getLogger().info("Invited to " + e.getChannel() + " by " + e.getUser() + ".");
    }

    @Override
    public void onJoin(JoinEvent e) {
        if (!e.getUser().getNick().equals(this.humanity.getBot().getUserBot().getNick())) return;
        this.humanity.getLogger().info("Joined " + e.getChannel().getName() + ".");
    }

    @Override
    public void onKick(KickEvent e) {
        if (!e.getUser().getNick().equals(this.humanity.getBot().getUserBot().getNick())) return;
        this.humanity.getLogger().info("Kicked from " + e.getChannel().getName() + ".");
    }

    @Override
    public void onPart(PartEvent e) {
        if (e.getChannel().getUsers().size() < 2) e.getChannel().send().part("Alone.");
        if (!e.getUser().getNick().equals(this.humanity.getBot().getUserBot().getNick())) return;
        this.humanity.getLogger().info("Parted from " + e.getChannel().getName() + ".");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onGenericMessage(GenericMessageEvent e) {
        if (!(e instanceof MessageEvent) && !(e instanceof PrivateMessageEvent)) return;
        final boolean isPrivateMessage = e instanceof PrivateMessageEvent;
        String message = e.getMessage();
        if (message.isEmpty()) return;
        if (message.startsWith(e.getBot().getNick()) && !isPrivateMessage) {
            message = e.getMessage().substring(e.getBot().getNick().length());
            if (message.charAt(0) != '.') return;
            message = message.substring(1);
            int parenIndex = message.indexOf('(');
            String command = message.substring(0, parenIndex);
            message = message.substring(parenIndex);
            if (!message.startsWith("(") || !message.endsWith(");")) return;
            message = message.substring(1, message.length() - 2);
            message = this.humanity.getPrefix() + command + " " + message;
        }
        if (message.charAt(0) != this.humanity.getPrefix() && !isPrivateMessage) return;
        final String[] split = message.trim().split(" ");
        final String commandString = (!isPrivateMessage) ? split[0].substring(1, split[0].length()) : split[0];
        final IRCCommand command = this.humanity.getCommandHandler().get(commandString);
        if (command == null) {
            if (isPrivateMessage) e.respond("No such command.");
            return;
        }
        final IRCCommand.CommandType commandType = command.getCommandType();
        if (!isPrivateMessage && commandType != IRCCommand.CommandType.MESSAGE && commandType != IRCCommand.CommandType.BOTH) {
            return;
        } else if (isPrivateMessage && commandType != IRCCommand.CommandType.PRIVATE && commandType != IRCCommand.CommandType.BOTH) {
            e.respond("No such command.");
            return;
        }
        this.humanity.getLogger().info(((isPrivateMessage) ? "" : ((MessageEvent) e).getChannel().getName() + "/") + e.getUser().getNick() + ": " + e.getMessage());
        try {
            command.onCommand(e, new CallInfo(commandString, ((isPrivateMessage) ? CallInfo.UsageType.PRIVATE : CallInfo.UsageType.MESSAGE)), ArrayUtils.subarray(split, 1, split.length));
        } catch (Throwable t) {
            t.printStackTrace();
            final StringBuilder sb = new StringBuilder("Unhandled command exception! ");
            sb.append(t.getClass().getSimpleName()).append(": ").append(t.getMessage());
            e.getUser().send().notice(sb.toString());
            this.humanity.getLogger().warning(sb.toString());
        }
    }

}
