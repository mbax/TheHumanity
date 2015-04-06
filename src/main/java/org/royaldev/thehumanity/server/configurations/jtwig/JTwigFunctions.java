package org.royaldev.thehumanity.server.configurations.jtwig;

import com.lyncode.jtwig.functions.annotations.JtwigFunction;
import com.lyncode.jtwig.functions.annotations.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.kitteh.irc.client.library.IRCFormat;

public class JTwigFunctions {

    @JtwigFunction(name = "friendly_enum")
    public String friendlyEnum(@Parameter String s) {
        return StringUtils.capitalize(s.replace('_', ' ').toLowerCase());
    }

    @JtwigFunction(name = "remove_irc_formatting")
    public String removeIRCFormatting(@Parameter String input) {
        return IRCFormat.stripFormating(input);
    }

}
