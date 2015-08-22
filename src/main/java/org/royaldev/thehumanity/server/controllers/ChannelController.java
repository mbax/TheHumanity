package org.royaldev.thehumanity.server.controllers;

import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.royaldev.thehumanity.server.services.channel.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

@Controller
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @ResponseBody
    @RequestMapping(value = "/api/channels", method = RequestMethod.GET, produces = APIHelper.PRODUCES)
    public String apiViewAllChannels(final HttpServletResponse response) {
        return APIHelper.makeObjectMapperJSON(
            response,
            om -> om.writeValueAsString(this.channelService.getAll().stream().map(Channel::getName).collect(Collectors.toSet()))
        );
    }

    @ResponseBody
    @RequestMapping(value = "/api/channel/{channel}", method = RequestMethod.GET, produces = APIHelper.PRODUCES)
    public String apiViewChannel(@PathVariable final String channel, final HttpServletResponse response) {
        final Channel c = this.channelService.getFromName("#" + channel);
        if (c == null || !this.channelService.getAll().contains(c)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return APIHelper.makeError("No such channel.");
        }
        return APIHelper.makeJSON(jw -> {
            jw.array();
            for (final User user : c.getUsers()) {
                jw
                    .object()
                    .key("nickname")
                    .value(user.getNick())
                    .key("user")
                    .value(user.getUser())
                    .key("hostname")
                    .value(user.getHost())
                    .endObject();
            }
            jw.endArray();
        });
    }

    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    public String viewAllChannels(final Model model) {
        model.addAttribute("channels", this.channelService.getAll());
        return "channels/index";
    }

    @RequestMapping(value = "/channel/{name}", method = RequestMethod.GET)
    public String viewChannel(@PathVariable String name, final Model model) {
        final Channel channel = this.channelService.getFromName("#" + name);
        if (channel == null || !this.channelService.getAll().contains(channel)) {
            return "redirect:/";
        }
        model.addAttribute("channel", channel);
        return "channels/channel";
    }

}
