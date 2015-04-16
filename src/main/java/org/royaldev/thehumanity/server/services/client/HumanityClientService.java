package org.royaldev.thehumanity.server.services.client;

import org.kitteh.irc.client.library.Client;
import org.royaldev.thehumanity.TheHumanity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HumanityClientService implements ClientService {

    @Autowired
    private TheHumanity humanity;

    @Override
    public Client getClient() {
        return this.humanity.getBot();
    }
}
