package org.royaldev.thehumanity.server.configurations.jtwig;

import com.lyncode.jtwig.functions.annotations.JtwigFunction;
import com.lyncode.jtwig.functions.annotations.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.kitteh.irc.client.library.IRCFormat;

public class JTwigFunctions {

    @JtwigFunction(name = "br2nl")
    public String br2nl(@Parameter String input) {
        return input.replaceAll("<br/?>", "\n");
    }

    @JtwigFunction(name = "irc_bold")
    public String emboldenIRC(@Parameter String input) {
        return input.replace(IRCFormat.BOLD.toString(), "<strong>").replace(IRCFormat.RESET.toString(), "</strong>");
    }

    @JtwigFunction(name = "friendly_enum")
    public String friendlyEnum(@Parameter String s) {
        return StringUtils.capitalize(s.replace('_', ' ').toLowerCase());
    }

    @JtwigFunction(name = "plural")
    public String pluralize(@Parameter String input, @Parameter int number) {
        return this.pluralize(input, number, "s");
    }

    @JtwigFunction(name = "plural")
    public String pluralize(@Parameter String input, @Parameter int number, @Parameter String suffix) {
        return number == 1 ? input : input + suffix;
    }

    @JtwigFunction(name = "remove_irc_formatting")
    public String removeIRCFormatting(@Parameter String input) {
        return IRCFormat.stripFormating(input);
    }

}
