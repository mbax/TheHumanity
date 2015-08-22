package org.royaldev.thehumanity.server.services.history;

import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.history.History;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HumanityHistoryService implements HistoryService {

    @Autowired
    private TheHumanity humanity;

    @Override
    public History getHistory() {
        return humanity.getHistory();
    }
}
