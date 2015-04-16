package org.royaldev.thehumanity.server.services.serverinfo;

import org.kitteh.irc.client.library.ServerInfo;
import org.royaldev.thehumanity.server.services.client.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HumanityServerInfoService implements ServerInfoService {

    @Autowired
    private ClientService clientService;

    @Override
    public ServerInfo getServerInfo() {
        return this.clientService.getClient().getServerInfo();
    }
}
