package org.royaldev.thehumanity.commands;

import org.jetbrains.annotations.NotNull;
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
    @NotNull String[] aliases() default {};

    /**
     * The CommandType for this command.
     *
     * @return CommandType
     */
    @NotNull CommandType commandType() default CommandType.BOTH;

    /**
     * A description of what this command does.
     *
     * @return Description
     */
    @NotNull String description();

    /**
     * The name of this command.
     *
     * @return Name
     */
    @NotNull String name();

    /**
     * The usage of this command.
     *
     * @return Usage
     */
    @NotNull String usage() default "<command>";

}
