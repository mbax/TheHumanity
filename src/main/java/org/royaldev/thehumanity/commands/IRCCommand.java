package org.royaldev.thehumanity.commands;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.helper.ActorEvent;

import java.util.Arrays;

/**
 * The basis for all commands handled by the bot.
 */
public abstract class IRCCommand {

    /**
     * This method is called when a command is received. Depending on what {@link #getCommandType()} returns, the event
     * passed to this method will either be a {@link org.kitteh.irc.client.library.event.channel.ChannelMessageEvent} or
     * a {@link org.kitteh.irc.client.library.event.user.PrivateMessageEvent}.
     *
     * @param event Event of receiving command
     * @param ci    Information received when this command was called
     * @param args  Arguments passed to the command
     */
    public abstract void onCommand(@NotNull final ActorEvent<User> event, @NotNull final CallInfo ci, @NotNull final String[] args);

    /**
     * Returns the first parameter, unless it is null, in which case, the second parameter is returned.
     *
     * @param expected Parameter to check
     * @param def      Default if expected is null
     * @return Never null
     */
    @NotNull
    private <T> T orDefault(@Nullable final T expected, @NotNull final T def) {
        Preconditions.checkNotNull(def, "def was null");
        return expected == null ? def : expected;
    }

    /**
     * Gets an array of names that can be used for this command.
     *
     * @return Array, not null
     */
    @NotNull
    public String[] getAliases() {
        return this.orDefault(this.getCommandAnnotation().aliases(), new String[0]);
    }

    public final Command getCommandAnnotation() {
        return this.getClass().getAnnotation(Command.class);
    }

    /**
     * This should return what type of command this is.
     *
     * @return CommandType
     */
    @NotNull
    public CommandType getCommandType() {
        return this.orDefault(this.getCommandAnnotation().commandType(), CommandType.BOTH);
    }

    /**
     * Gets a brief description of the command.
     *
     * @return <em>Brief</em> description
     */
    @NotNull
    public String getDescription() {
        return this.orDefault(this.getCommandAnnotation().description(), this.getName());
    }

    /**
     * This should return the name of the command. An example would be "ping"
     * <br/>
     * Case does not matter; do not include a command prefix.
     *
     * @return Name of the command.
     */
    @NotNull
    public String getName() {
        return this.orDefault(this.getCommandAnnotation().name(), "invalid_command");
    }

    /**
     * Gets the usage for this command. Should follow this format:
     * <pre>"&lt;command&gt; [required] (optional)"</pre>
     * Do not replace "&lt;command&gt;" with the actual name of the command; that will automatically be done.
     *
     * @return Usage string
     */
    @NotNull
    public String getUsage() {
        return this.orDefault(this.getCommandAnnotation().usage(), "<command>");
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", this.getName())
            .add("description", this.getDescription())
            .add("aliases", Arrays.toString(this.getAliases()))
            .add("commandType", this.getCommandType())
            .add("usage", this.getUsage())
            .toString();
    }

    /**
     * CommandType defines where a command can be used.
     */
    public enum CommandType {
        /**
         * Command can be used in channel messages only.
         */
        MESSAGE("Channel message only"),
        /**
         * Command can be used in private messages only.
         */
        PRIVATE("Private message only"),
        /**
         * Command can be used in either channel or private message.
         */
        BOTH("Channel or private message");

        private final String description;

        CommandType(String description) {
            this.description = description;
        }

        /**
         * Gets the description of where the command can be used.
         *
         * @return Description
         */
        public String getDescription() {
            return description;
        }
    }
}
