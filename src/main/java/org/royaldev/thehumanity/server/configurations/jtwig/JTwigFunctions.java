package org.royaldev.thehumanity.server.configurations.jtwig;

import com.lyncode.jtwig.functions.annotations.JtwigFunction;
import com.lyncode.jtwig.functions.annotations.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.kitteh.irc.client.library.IRCFormat;

public class JTwigFunctions {

    @JtwigFunction(name = "br2nl")
    public String br2nl(@Parameter final String input) {
        return input.replaceAll("<br/?>", "\n");
    }

    @JtwigFunction(name = "irc_bold")
    public String emboldenIRC(@Parameter final String input) {
        return input.replace(IRCFormat.BOLD.toString(), "<strong>").replace(IRCFormat.RESET.toString(), "</strong>");
    }

    @JtwigFunction(name = "friendly_enum")
    public String friendlyEnum(@Parameter final String s) {
        return StringUtils.capitalize(s.replace('_', ' ').toLowerCase());
    }

    @JtwigFunction(name = "plural")
    public String pluralize(@Parameter final String input, @Parameter final int number) {
        return this.pluralize(input, number, "s");
    }

    @JtwigFunction(name = "plural")
    public String pluralize(@Parameter final String input, @Parameter final int number, @Parameter final String suffix) {
        return number == 1 ? input : input + suffix;
    }

    @JtwigFunction(name = "remove_irc_colors")
    public String removeIRCColors(@Parameter final String input) {
        return IRCFormat.stripColor(input);
    }

    @JtwigFunction(name = "remove_irc_formatting")
    public String removeIRCFormatting(@Parameter final String input) {
        return IRCFormat.stripFormating(input);
    }

}
