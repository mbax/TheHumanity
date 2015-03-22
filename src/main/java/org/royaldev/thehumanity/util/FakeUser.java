package org.royaldev.thehumanity.util;

import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.User;

import java.util.Set;

public class FakeUser implements User {

    private final String nick;

    public FakeUser(final String nick) {
        this.nick = nick;
    }

    @Override
    public Set<String> getChannels() {
        return null;
    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public String getNick() {
        return this.nick;
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public Client getClient() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public String getMessagingName() {
        return null;
    }

    @Override
    public void sendCTCPMessage(final String s) {

    }

    @Override
    public void sendMessage(final String s) {

    }

    @Override
    public void sendNotice(final String s) {

    }
}
