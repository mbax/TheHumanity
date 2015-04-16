package org.royaldev.thehumanity.server.controllers.advisors;

import org.royaldev.thehumanity.server.services.client.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ClientAdvisor {

    @Autowired
    private ClientService clientService;

    @ModelAttribute
    public void addClient(final Model model) {
        model.addAttribute("client", this.clientService.getClient());
    }

}
