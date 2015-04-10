package org.royaldev.thehumanity.server.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.royaldev.thehumanity.game.GameSnapshot;
import org.royaldev.thehumanity.game.round.RoundSnapshot;
import org.royaldev.thehumanity.history.History;
import org.royaldev.thehumanity.server.services.history.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Controller
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @ResponseBody
    @RequestMapping(value = "/api/history", method = RequestMethod.GET, produces = APIHelper.PRODUCES)
    public String apiHistory() {
        final History h = this.historyService.getHistory();
        final File historyFolder = h.getHistoryFolder();
        return APIHelper.makeObjectMapperJSON(
            om -> om.writeValueAsString(
                !historyFolder.exists() || !historyFolder.isDirectory()
                    ? new String[0]
                    : historyFolder.list()
            )
        );
    }

    @ResponseBody
    @RequestMapping(value = "/api/history/{channel}", method = RequestMethod.GET, produces = APIHelper.PRODUCES)
    public String apiHistoryChannel(@PathVariable final String channel, final HttpServletResponse response) {
        return APIHelper.makeObjectMapperJSON(response, om -> om.writeValueAsString(
            this.historyService.getHistory().getAllGameNumbers("#" + channel)
        ));
    }

    @ResponseBody
    @RequestMapping(value = "/api/history/{channel}/{game}", method = RequestMethod.GET, produces = APIHelper.PRODUCES)
    public String apiHistoryChannelGame(@PathVariable final String channel, @PathVariable final int game, final HttpServletResponse response) {
        final GameSnapshot gs;
        try {
            gs = this.historyService.getHistory().loadGameSnapshot("#" + channel, game);
        } catch (final IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return APIHelper.makeError(ex);
        }
        if (gs == null) {
            return APIHelper.makeError("No such game.");
        }
        return APIHelper.makeObjectMapperJSON(
            response,
            om -> {
                om.addMixInAnnotations(GameSnapshot.class, GameSnapshotMixin.class);
                return om.writeValueAsString(gs);
            }
        );
    }

    @ResponseBody
    @RequestMapping(value = "/api/history/{channel}/{game}/{round}", method = RequestMethod.GET, produces = APIHelper.PRODUCES)
    public String apiHistoryChannelGameRound(@PathVariable final String channel, @PathVariable final int game, @PathVariable final int round, final HttpServletResponse response) {
        final GameSnapshot gs;
        try {
            gs = this.historyService.getHistory().loadGameSnapshot("#" + channel, game);
        } catch (final IllegalArgumentException ex) {
            return APIHelper.makeError(ex);
        }
        if (gs == null) {
            return APIHelper.makeError("No such game.");
        }
        try {
            return gs.getPreviousRounds().get(round - 1).toJSON();
        } catch (final IndexOutOfBoundsException ex) {
            return APIHelper.makeError("No such round.");
        }
    }

    private interface GameSnapshotMixin {

        @JsonIgnore
        List<RoundSnapshot> getPreviousRounds();
    }

}
