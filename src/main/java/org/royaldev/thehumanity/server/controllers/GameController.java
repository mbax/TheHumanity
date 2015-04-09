package org.royaldev.thehumanity.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONWriter;
import org.kitteh.irc.client.library.element.Channel;
import org.royaldev.thehumanity.game.round.CurrentRound;
import org.royaldev.thehumanity.game.Game;
import org.royaldev.thehumanity.game.HouseRule;
import org.royaldev.thehumanity.game.round.Round.RoundStage;
import org.royaldev.thehumanity.cards.play.Play;
import org.royaldev.thehumanity.cards.packs.CardPack;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.player.Player;
import org.royaldev.thehumanity.server.services.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @ResponseBody
    @RequestMapping(value = "/api/game/{channel}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String apiViewGame(@PathVariable String channel, final HttpServletResponse response) {
        final Game g = this.gameService.getFromChannelName("#" + channel);
        if (g == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return APIHelper.makeError("No such game.");
        }
        final StringWriter sw = new StringWriter();
        final JSONWriter jw = new JSONWriter(sw);
        jw
            .object()
            .key("channel")
            .value(g.getChannel().getName())
            .key("players")
            .value(g.getPlayers().stream().map(p -> p.getUser().getNick()).collect(Collectors.toList()))
            .key("historicPlayers")
            .value(g.getHistoricPlayers().stream().map(p -> p.getUser().getNick()).collect(Collectors.toList()))
            .key("host")
            .value(g.getHost().getUser().getNick())
            .key("scores")
            .value(g.getHistoricPlayers().stream().collect(Collectors.toMap(p -> p.getUser().getNick(), Player::getScore)))
            .key("houseRules")
            .value(g.getHouseRules().stream().map(HouseRule::getFriendlyName).collect(Collectors.toList()))
            .key("cardPacks")
            .value(g.getDeck().getCardPacks().stream().map(CardPack::getName).collect(Collectors.toList()))
            .key("gameStatus")
            .value(g.getGameStatus())
            .key("currentRound");
        final CurrentRound round = g.getCurrentRound();
        if (round == null) {
            jw.value(null);
        } else {
            final List<List<String>> plays = round.getPlays().stream()
                .map(Play::getWhiteCards) // Convert each Play to a List<WhiteCard>
                .map(
                    list -> list.stream() // Stream each WhiteCard
                        .map(WhiteCard::getText) // Convert each WhiteCard to its text form
                        .collect(Collectors.toList()) // Collect the Strings into a List<String>
                ) // Map the List<WhiteCard> to List<String>
                .collect(Collectors.toList()); // Collect the lists into a List<List<String>>
            Collections.shuffle(plays); // shuffle for anonymity
            jw
                .object()
                .key("number")
                .value(round.getNumber())
                .key("czar")
                .value(round.getCzar() == null ? null : round.getCzar().getUser().getNick())
                .key("blackCard")
                .value(round.getBlackCard())
                .key("stage")
                .value(round.getCurrentStage())
                .key("plays")
                .value(round.getCurrentStage() == RoundStage.WAITING_FOR_CZAR ? plays : new String[0])
                .key("skippedPlayers")
                .value(round.getSkippedPlayers().stream().map(p -> p.getUser().getNick()).collect(Collectors.toList()))
                .endObject();
        }
        jw.endObject();
        return sw.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/api/games", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String apiViewGames(final HttpServletResponse response) {
        final ObjectMapper om = new ObjectMapper();
        try {
            return om.writeValueAsString(this.gameService.getAll().keySet().stream().map(Channel::getName).collect(Collectors.toList()));
        } catch (final JsonProcessingException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return APIHelper.makeError(ex);
        }
    }

    @RequestMapping(value = "/game/{channel}", method = RequestMethod.GET)
    public String gameInChannel(@PathVariable final String channel, final Model model) {
        final Game g = this.gameService.getFromChannelName("#" + channel);
        if (g == null) {
            return "redirect:/";
        }
        model.addAttribute("game", g);
        return "games/game";
    }

    @RequestMapping(value = "/games", method = RequestMethod.GET)
    public String viewGames(final Model model) {
        model.addAttribute("games", this.gameService.getAll());
        return "games/index";
    }

}
