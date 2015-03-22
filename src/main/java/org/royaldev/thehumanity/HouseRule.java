package org.royaldev.thehumanity;

public enum HouseRule {
    HAPPY_ENDING("Happy Ending"),
    REBOOTING_THE_UNIVERSE("Rebooting the Universe"),
    PACKING_HEAT("Packing Heat"),
    RANDO_CARDRISSIAN("Rando Cardrissian"),
    GOD_IS_DEAD("God is Dead"),
    SERIOUS_BUSINESS("Serious Business"),
    NEVER_HAVE_I_EVER("Never Have I Ever");

    private final String friendlyName;

    HouseRule(final String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public static HouseRule getByFriendlyName(final String friendlyName) {
        for (final HouseRule hr : HouseRule.values()) {
            if (hr.getFriendlyName().equalsIgnoreCase(friendlyName)) {
                return hr;
            }
        }
        return null;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    @Override
    public String toString() {
        return this.getFriendlyName();
    }
}
