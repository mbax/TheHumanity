package org.royaldev.thehumanity.server.controllers;

import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.server.services.cardpack.CardPackService;
import org.royaldev.thehumanity.server.services.channel.ChannelService;
import org.royaldev.thehumanity.server.services.game.GameService;
import org.royaldev.thehumanity.server.services.serverinfo.ServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(name = "/")
public class IndexController {

    @Autowired
    private TheHumanity humanity;
    @Autowired
    private ServerInfoService serverInfoService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private GameService gameService;
    @Autowired
    private CardPackService cardPackService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(final Model model) {
        model.addAttribute("humanity", this.humanity);
        model.addAttribute("serverInfo", this.serverInfoService.getServerInfo());
        model.addAttribute("channels", this.channelService.getAll());
        model.addAttribute("games", this.gameService.getAll());
        model.addAttribute("cardPacks", this.cardPackService.getAll());
        return "index";
    }

}
