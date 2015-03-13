package org.royaldev.thehumanity.commands;

import org.royaldev.thehumanity.commands.IRCCommand.CommandType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    /**
     *
     * @return
     */
    String[] aliases() default {};

    CommandType commandType() default CommandType.BOTH;

    String description();

    String name();

    String usage() default "<command>";

}
