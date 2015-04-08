package org.royaldev.thehumanity.server.controllers;

import org.royaldev.thehumanity.cards.packs.CardPack;
import org.royaldev.thehumanity.server.services.cardpack.CardPackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CardPackController {

    @Autowired
    private CardPackService cardPackService;

    @RequestMapping(value = "/pack/{pack}", method = RequestMethod.GET)
    public String viewPack(@PathVariable String pack, final Model model) {
        final CardPack cp = this.cardPackService.getFromName(pack);
        if (cp == null) {
            return "redirect:/";
        }
        model.addAttribute("pack", cp);
        return "packs/pack/index";
    }

    @RequestMapping(value = "/packs", method = RequestMethod.GET)
    public String viewPacks(final Model model) {
        model.addAttribute("packs", this.cardPackService.getAll());
        return "packs/index";
    }

}
