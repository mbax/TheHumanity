package org.royaldev.thehumanity.commands;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * This class contains information on the calling of any given command.
 */
public class CallInfo {

    @NotNull
    private final String label;
    @NotNull
    private final UsageType usageType;

    /**
     * Creates a new CallInfo object initialized with the data necessary.
     *
     * @param label     User-provided command name
     * @param usageType Where the command was used
     */
    public CallInfo(@NotNull String label, @NotNull UsageType usageType) {
        Preconditions.checkNotNull(label, "label was null");
        Preconditions.checkNotNull(usageType, "usageType was null");
        this.label = label;
        this.usageType = usageType;
    }

    /**
     * Returns the name of the command given by the user.
     *
     * @return Name of the command (possibly alias)
     */
    @NotNull
    public String getLabel() {
        return this.label;
    }

    /**
     * Returns where the usage occurred.
     *
     * @return UsageType
     */
    @NotNull
    public UsageType getUsageType() {
        return this.usageType;
    }

    /**
     * Usage of the command.
     */
    public enum UsageType {
        /**
         * Command was used in a channel message.
         */
        MESSAGE,
        /**
         * Command was used in a private message.
         */
        PRIVATE
    }
}
