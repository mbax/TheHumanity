package org.royaldev.thehumanity.ping;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.royaldev.thehumanity.TheHumanity;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * Handles WhoX lines.
 */
public class WhoX implements Consumer<String> {

    private final TheHumanity humanity;
    // Account, Nickname
    private final Multimap<String, String> accounts = ArrayListMultimap.create();
    private final Object finishObject = new Object();
    private boolean receiving = false;

    public WhoX(final TheHumanity instance) {
        this.humanity = instance;
    }

    /**
     * Processes ACCOUNT messages from the server.
     *
     * @param parts Parts of the message
     */
    private void account(@NotNull final String[] parts) {
        Preconditions.checkNotNull(parts, "parts was null");
        final String nick = parts[0].substring(1).split("!")[0];
        final String account = parts[2];
        if (account.equals("*")) {
            this.addAccount(nick, null);
            return;
        }
        this.addAccount(nick, account);
    }

    /**
     * Internal method to send the actual WhoX to the server for any given target.
     *
     * @param target Target to send WhoX to
     */
    private void sendWhoX(@NotNull final String target) {
        Preconditions.checkNotNull(target, "target was null");
        this.humanity.getBot().sendRawLine("WHO " + target + " %na");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(@NotNull final String s) {
        Preconditions.checkNotNull(s, "s was null");
        if (this.humanity.isDebugMode()) {
            System.out.println("input  = " + s);
        }
        final String[] parts = s.split(" ");
        if (parts.length < 2) {
            return;
        }
        if (parts[1].equals("ACCOUNT")) {
            this.account(parts);
            return;
        }
        if (parts.length < 5) {
            return;
        }
        final int code;
        try {
            code = Integer.parseInt(parts[1]);
        } catch (final NumberFormatException ex) {
            return;
        }
        if (code == 315) {
            this.receiving = false;
            synchronized (this.finishObject) {
                this.finishObject.notifyAll();
            }
            return;
        }
        if (code != 354) {
            return;
        }
        this.receiving = true;
        final String nick = parts[3];
        final String account = parts[4];
        if (account.equals("0")) {
            // Put null in the accounts map. This way, we don't query the server every time someone without an account
            // tries to use a command.
            this.addAccount(nick, null);
            return;
        }
        if (!this.hasAccountMapping(nick)) {
            this.accounts.put(account, nick);
        }
    }

    /**
     * Sets the account for a given nick. Accounts may have multiple nicks associated with them. The nick cannot be
     * null, but the account may be null to indicate no account.
     *
     * @param nick    Nick to associate with an account
     * @param account Account to associate the nick with
     */
    public void addAccount(@NotNull final String nick, @Nullable final String account) {
        Preconditions.checkNotNull(nick, "nick was null");
        this.accounts.put(account, nick);
    }

    /**
     * Gets the account for the given nick. If the nick has no account or is unknown, null will be returned. Ensure that
     * {@link #hasAccountMapping(String)} is called to understand what a null return value means.
     *
     * @param nick Nick to get account for
     * @return Account name or null
     * @see #hasAccountMapping(String)
     */
    @Nullable
    public String getAccount(@NotNull final String nick) {
        Preconditions.checkNotNull(nick, "nick was null");
        for (final Entry<String, String> entry : this.accounts.entries()) {
            if (!nick.equalsIgnoreCase(entry.getValue())) continue;
            return entry.getKey();
        }
        return null;
    }

    /**
     * Gets the object that will be {@link Object#notifyAll() notified} when a WhoX is done being processed.
     *
     * @return Object
     */
    @NotNull
    public Object getFinishObject() {
        return this.finishObject;
    }

    /**
     * Gets all the nicks associated with the given account.
     *
     * @param account Account to get nicks from
     * @return Collection of nicks
     */
    @NotNull
    public Collection<String> getNicks(@Nullable final String account) {
        return this.accounts.get(account);
    }

    /**
     * Checks if a nick has an account associated with it.
     *
     * @param nick Nick to check
     * @return true if the nick is associated with an account
     */
    public boolean hasAccountMapping(@NotNull final String nick) {
        Preconditions.checkNotNull(nick, "nick was null");
        return this.accounts.containsValue(nick);
    }

    /**
     * Checks if an account has a nick associated with it.
     *
     * @param account Account to check
     * @return true if the account is associated with a nick
     */
    public boolean hasNickMapping(@Nullable final String account) {
        return this.accounts.containsKey(account);
    }

    /**
     * Returns true if and only if at least one response to a WhoX has been received from the server, and the end of who
     * response has not been received.
     *
     * @return true if receiving, false if not
     */
    public boolean isReceiving() {
        return this.receiving;
    }

    /**
     * Removes an account/nick mapping from the list.
     *
     * @param nick    Nick to remove
     * @param account Account to remove
     */
    public void remove(@NotNull final String nick, @Nullable final String account) {
        Preconditions.checkNotNull(nick, "nick was null");
        this.accounts.remove(account, nick);
    }

    /**
     * Requests a WhoX from the server for the given user.
     *
     * @param u User to send the WhoX to
     */
    public void sendWhoX(@NotNull final User u) {
        Preconditions.checkNotNull(u, "u was null");
        this.sendWhoX(u.getMessagingName());
    }

    /**
     * Requests a WhoX from the server for the given channel.
     *
     * @param c Channel to send the WhoX to.
     */
    public void sendWhoX(@NotNull final Channel c) {
        Preconditions.checkNotNull(c, "c was null");
        this.sendWhoX(c.getMessagingName());
    }
}
