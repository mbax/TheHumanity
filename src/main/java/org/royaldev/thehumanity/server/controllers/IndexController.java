package org.royaldev.thehumanity.server.controllers;

import org.royaldev.thehumanity.TheHumanity;
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

    @RequestMapping(method = RequestMethod.GET)
    public String index(final Model model) {
        model.addAttribute("humanity", this.humanity);
        return "index";
    }

}
