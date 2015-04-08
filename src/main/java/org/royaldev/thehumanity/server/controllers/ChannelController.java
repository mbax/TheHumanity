package org.royaldev.thehumanity.server.controllers;

import org.kitteh.irc.client.library.element.Channel;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.server.services.channel.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ChannelController {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private TheHumanity humanity;

    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    public String viewAllChannels(final Model model) {
        model.addAttribute("channels", this.channelService.getAll());
        return "channels/index";
    }

    @RequestMapping(value = "/channel/{name}", method = RequestMethod.GET)
    public String viewChannel(@PathVariable String name, final Model model) {
        final Channel channel = this.channelService.getFromName("#" + name);
        if (channel == null || !this.humanity.getBot().getChannels().contains(channel)) {
            return "redirect:/";
        }
        model.addAttribute("channel", channel);
        return "channels/channel";
    }

}
