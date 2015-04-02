package org.royaldev.thehumanity.ping;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PingRegistration implements Serializable {

    private static final long serialVersionUID = 1338L;

    private final String servicesAccount;
    private final List<PingRegistrationOption> options = Lists.newArrayList();

    public PingRegistration(@NotNull final String servicesAccount) {
        Preconditions.checkNotNull(servicesAccount, "servicesAccount was null");
        this.servicesAccount = servicesAccount;
    }

    /**
     * Adds an option to this registration. If the option is already added, this will return false.
     *
     * @param option Option to add
     * @return false if option is already present or the result of {@link List#add(Object)}.
     */
    public boolean addOption(@NotNull final PingRegistrationOption option) {
        Preconditions.checkNotNull(option, "option was null");
        return !this.options.contains(option) && this.options.add(option);
    }

    /**
     * Gets this registration's options.
     *
     * @return Unmodifiable list of options
     */
    public List<PingRegistrationOption> getOptions() {
        return Collections.unmodifiableList(this.options);
    }

    /**
     * Gets the services account associated with this registration.
     *
     * @return Account name
     */
    public String getServicesAccount() {
        return this.servicesAccount;
    }

    /**
     * Convenience method for {@code getOptions().contains(option}.
     *
     * @param option Option to check for
     * @return Result of {@link List#contains(Object)}.
     */
    public boolean hasOption(@NotNull final PingRegistrationOption option) {
        Preconditions.checkNotNull(option, "option was null");
        return this.getOptions().contains(option);
    }

    /**
     * Removes an option from this registration. If the option is already removed, this will return false.
     *
     * @param option Option to remove
     * @return false if option is not present or the result of {@link List#remove(Object)}.
     */
    public boolean removeOption(@NotNull final PingRegistrationOption option) {
        Preconditions.checkNotNull(option, "option was null");
        return this.options.contains(option) && this.options.remove(option);
    }
}
