package org.royaldev.thehumanity.server.controllers.advisors;

import org.royaldev.thehumanity.TheHumanity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class HumanityAdvisor {

    @Autowired
    private TheHumanity humanity;

    @ModelAttribute
    public void addHumanity(final Model model) {
        model.addAttribute("humanity", this.humanity);
    }

}
