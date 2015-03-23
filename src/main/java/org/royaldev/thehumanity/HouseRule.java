package org.royaldev.thehumanity;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum HouseRule {
    HAPPY_ENDING("Happy Ending"),
    REBOOTING_THE_UNIVERSE("Rebooting the Universe"),
    PACKING_HEAT("Packing Heat"),
    RANDO_CARDRISSIAN("Rando Cardrissian"),
    GOD_IS_DEAD("God is Dead"),
    SERIOUS_BUSINESS("Serious Business"),
    NEVER_HAVE_I_EVER("Never Have I Ever");

    @NotNull
    private final String friendlyName;

    HouseRule(@NotNull final String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Nullable
    public static HouseRule getByFriendlyName(@NotNull final String friendlyName) {
        Validate.notNull(friendlyName, "friendlyName was null");
        return Arrays.stream(HouseRule.values())
            .filter(hr -> hr.getFriendlyName().equalsIgnoreCase(friendlyName))
            .findFirst()
            .orElse(null);
    }

    @NotNull
    public String getFriendlyName() {
        return this.friendlyName;
    }

    @Override
    public String toString() {
        return this.getFriendlyName();
    }
}
