package org.royaldev.thehumanity.commands.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.IRCCommand;
import org.royaldev.thehumanity.commands.NoticeableCommand;

import java.util.Arrays;

public class HelpCommand implements NoticeableCommand {

    private final TheHumanity humanity;

    public HelpCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    @Override
    public String getDescription() {
        return "Gets the help for all commands.";
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public void onCommand(GenericMessageEvent event, CallInfo ci, String[] args) {
        final User u = event.getUser();
        final StringBuilder sb = new StringBuilder();
        for (final IRCCommand ic : this.humanity.getCommandHandler().getAll()) {
            sb.append(this.humanity.getPrefix()).append(ic.getName()).append(" – ").append(ic.getDescription()).append("\n");
            sb.append("  Usage: ").append(ic.getUsage().replaceAll("<command>", ic.getName())).append("\n");
            sb.append("  Aliases: ").append(Arrays.toString(ic.getAliases())).append("\n");
        }
        try {
            final HttpResponse<JsonNode> response = Unirest.post("http://hasteb.in/documents").body(sb.toString()).asJson();
            this.notice(u, "http://hasteb.in/" + response.getBody().getObject().getString("key"));
        } catch (final UnirestException ex) {
            this.notice(u, "An error occurred: " + ex.getMessage());
        }
    }
}
