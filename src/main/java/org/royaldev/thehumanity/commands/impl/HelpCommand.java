package org.royaldev.thehumanity.commands.impl;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.HttpResponse;

import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.IRCCommand;
import org.royaldev.thehumanity.commands.NoticeableCommand;

import java.util.Arrays;
import java.lang.StringBuilder;

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
        String[] helptext = new String[];
        for (final IRCCommand ic : this.humanity.getCommandHandler().getAll()) {
            helptext.append(this.humanity.getPrefix() + ic.getName() + " – " + ic.getDescription());
            helptext.append("  Usage: " + ic.getUsage().replaceAll("<command>", ic.getName()));
            helptext.append("  Aliases: " + Arrays.toString(ic.getAliases()));
        }
        HttpResponse<JsonNode> jsonResponse = Unirest.post("http://hasteb.in/documents")
                                                       .body(String.join("\n", helptext))
                                                       .asJson();
        this.notice(u, "http://hasteb.in/" + jsonResponse.getBody()["key"]);
    }
}
