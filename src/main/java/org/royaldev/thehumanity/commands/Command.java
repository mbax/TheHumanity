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
     * Gets an array of names that can be used for this command.
     *
     * @return Array
     */
    String[] aliases() default {};

    CommandType commandType() default CommandType.BOTH;

    String description();

    String name();

    String usage() default "<command>";

}
