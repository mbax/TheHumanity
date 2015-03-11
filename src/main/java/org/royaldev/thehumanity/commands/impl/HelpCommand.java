package org.royaldev.thehumanity.commands.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONWriter;
import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.commands.CallInfo;
import org.royaldev.thehumanity.commands.IRCCommand;
import org.royaldev.thehumanity.commands.NoticeableCommand;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HelpCommand implements NoticeableCommand {

    @SuppressWarnings("StaticNonFinalField")
    private static HelpGist currentGist;
    private final TheHumanity humanity;

    public HelpCommand(final TheHumanity instance) {
        this.humanity = instance;
    }

    private String getNames() {
        return this.humanity.getCommandHandler().getAll().stream().map(IRCCommand::getName).sorted().collect(Collectors.joining());
    }

    private boolean isNewGistNeeded() {
        return HelpCommand.currentGist == null || !HelpCommand.currentGist.getNames().equals(this.getNames());
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
            sb.append("## ").append(this.humanity.getPrefix()).append(ic.getName()).append("\n");
            sb.append("*").append(ic.getDescription()).append("*  \n");
            if (!"<command>".equalsIgnoreCase(ic.getUsage())) {
                sb.append("**Usage:** ").append(ic.getUsage().replaceAll("<command>", ic.getName())).append("  \n");
            }
            if (ic.getAliases().length > 0) {
                sb.append("**Aliases:** ").append(Arrays.toString(ic.getAliases())).append("\n");
            }
        }
        if (this.isNewGistNeeded()) {
            final StringWriter sw = new StringWriter();
            final JSONWriter jw = new JSONWriter(sw);
            jw.object().key("files")
                .object().key("help.md")
                .object().key("content").value(sb.toString())
                .endObject().endObject().endObject();
            try {
                final HttpResponse<JsonNode> response = Unirest
                    .post("https://api.github.com/gists")
                    .body(sw.toString())
                    .asJson();
                final String gist = response.getBody().getObject().getString("html_url");
                HelpCommand.currentGist = new HelpGist(gist, this.getNames());
                this.notice(u, gist);
            } catch (final UnirestException ex) {
                this.notice(u, "An error occurred: " + ex.getMessage());
            }
        } else {
            this.notice(u, HelpCommand.currentGist.getGist());
        }
    }

    private class HelpGist {

        private final String gist;
        private final String names;

        private HelpGist(final String gist, final String names) {
            this.gist = gist;
            this.names = names;
        }

        private String getGist() {
            return this.gist;
        }

        private String getNames() {
            return this.names;
        }
    }
}
