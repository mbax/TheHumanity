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

    /**
     * The CommandType for this command.
     *
     * @return CommandType
     */
    CommandType commandType() default CommandType.BOTH;

    /**
     * A description of what this command does.
     *
     * @return Description
     */
    String description();

    /**
     * The name of this command.
     *
     * @return Name
     */
    String name();

    /**
     * The usage of this command.
     *
     * @return Usage
     */
    String usage() default "<command>";

}
