package org.royaldev.thehumanity.server.controllers;

import org.royaldev.thehumanity.Game;
import org.royaldev.thehumanity.server.services.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

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
