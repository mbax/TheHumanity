package org.royaldev.thehumanity.server.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.royaldev.thehumanity.cards.Card;
import org.royaldev.thehumanity.cards.packs.CardPack;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.server.services.cardpack.CardPackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class CardPackController {

    @Autowired
    private CardPackService cardPackService;

    @ResponseBody
    @RequestMapping(value = "/api/pack/{name}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String apiPack(@PathVariable String name, final HttpServletResponse response) {
        final CardPack cp = this.cardPackService.getFromName(name);
        if (cp == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return APIHelper.makeError("No such pack.");
        }
        final ObjectMapper om = new ObjectMapper();
        try {
            om.addMixInAnnotations(Card.class, CardMixIn.class);
            return om.writeValueAsString(cp);
        } catch (final JsonProcessingException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return APIHelper.makeError(ex);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/api/packs", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String apiPacks(final HttpServletResponse response) {
        final ObjectMapper om = new ObjectMapper();
        try {
            om.addMixInAnnotations(CardPack.class, CardPackMixIn.class);
            return om.writeValueAsString(this.cardPackService.getAll());
        } catch (final JsonProcessingException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return APIHelper.makeError(ex);
        }
    }

    @RequestMapping(value = "/pack/{pack}", method = RequestMethod.GET)
    public String viewPack(@PathVariable String pack, final Model model) {
        final CardPack cp = this.cardPackService.getFromName(pack);
        if (cp == null) {
            return "redirect:/";
        }
        model.addAttribute("pack", cp);
        return "packs/pack";
    }

    @RequestMapping(value = "/packs", method = RequestMethod.GET)
    public String viewPacks(final Model model) {
        model.addAttribute("packs", this.cardPackService.getAll());
        return "packs/index";
    }

    interface CardPackMixIn {

        @JsonIgnore
        List<BlackCard> getBlackCards();

        @JsonIgnore
        List<WhiteCard> getWhiteCards();
    }

    interface CardMixIn {

        @JsonIgnore
        CardPack getCardPack();
    }

}
