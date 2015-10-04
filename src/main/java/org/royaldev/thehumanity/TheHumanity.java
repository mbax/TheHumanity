package org.royaldev.thehumanity;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.hash.Hashing;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONWriter;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.ClientBuilder;
import org.kitteh.irc.client.library.EventManager;
import org.kitteh.irc.client.library.auth.protocol.NickServ;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.ChannelUserMode;
import org.kitteh.irc.client.library.element.User;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;
import org.kohsuke.args4j.spi.CharOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.kohsuke.args4j.spi.StringOptionHandler;
import org.royaldev.thehumanity.cards.cardcast.CardcastFetcher;
import org.royaldev.thehumanity.cards.packs.CardPack;
import org.royaldev.thehumanity.cards.packs.CardPackParser;
import org.royaldev.thehumanity.cards.packs.CardcastCardPack;
import org.royaldev.thehumanity.commands.impl.CardCountsCommand;
import org.royaldev.thehumanity.commands.impl.CardsCommand;
import org.royaldev.thehumanity.commands.impl.HelpCommand;
import org.royaldev.thehumanity.commands.impl.HostCommand;
import org.royaldev.thehumanity.commands.impl.JoinGameCommand;
import org.royaldev.thehumanity.commands.impl.KickCommand;
import org.royaldev.thehumanity.commands.impl.LeaveGameCommand;
import org.royaldev.thehumanity.commands.impl.LoadCardPackCommand;
import org.royaldev.thehumanity.commands.impl.NeverHaveIEverCommand;
import org.royaldev.thehumanity.commands.impl.PacksCommand;
import org.royaldev.thehumanity.commands.impl.PickCardCommand;
import org.royaldev.thehumanity.commands.impl.RebootTheUniverseCommand;
import org.royaldev.thehumanity.commands.impl.ScoreCommand;
import org.royaldev.thehumanity.commands.impl.SkipCommand;
import org.royaldev.thehumanity.commands.impl.StartGameCommand;
import org.royaldev.thehumanity.commands.impl.StopGameCommand;
import org.royaldev.thehumanity.commands.impl.VersionCommand;
import org.royaldev.thehumanity.commands.impl.WhoCommand;
import org.royaldev.thehumanity.commands.impl.game.GameCommand;
import org.royaldev.thehumanity.commands.impl.ping.PingListCommand;
import org.royaldev.thehumanity.game.Game;
import org.royaldev.thehumanity.handlers.CommandHandler;
import org.royaldev.thehumanity.history.History;
import org.royaldev.thehumanity.ping.PingRegistry;
import org.royaldev.thehumanity.ping.WhoX;
import org.royaldev.thehumanity.ping.task.SavePingRegistryTask;
import org.royaldev.thehumanity.server.GameServer;
import org.royaldev.thehumanity.server.configurations.HumanityConfiguration;
import org.royaldev.thehumanity.util.Pair;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings({"MismatchedReadAndWriteOfArray", "FieldMayBeFinal"})
public class TheHumanity {

    private final List<CardPack> loadedCardPacks = Collections.synchronizedList(new ArrayList<>());
    private final Client bot;
    private final CommandHandler ch = new CommandHandler();
    private final Map<Channel, Game> games = new HashMap<>();
    private final Cache<String, Pair<String, String>> gistCache = CacheBuilder.newBuilder().build();
    private final Logger l = Logger.getLogger("org.royaldev.thehumanity");
    private final ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);
    private final PingRegistry pingRegistry;
    private final WhoX whoX = new WhoX(this);
    private final History history = new History(this);
    @Nullable
    private final GameServer gameServer;
    @Option(name = "-c", usage = "Channels to join.", required = true, handler = StringArrayOptionHandler.class)
    private String[] channels;
    @Option(name = "-s", usage = "Server to connect to.", required = true, handler = StringOptionHandler.class)
    private String server;
    @Option(name = "-p", usage = "Server port.", handler = IntOptionHandler.class)
    private int serverPort = 6667;
    @Option(name = "-P", usage = "Server password.", handler = StringOptionHandler.class)
    private String serverPassword = "";
    @Option(name = "-Z", usage = "Connect to the server using SSL?", handler = BooleanOptionHandler.class)
    private boolean ssl = false;
    @Option(name = "-n", usage = "Nickname for the bot to use.", handler = StringOptionHandler.class)
    private String nickname = "TheHumanity";
    @Option(name = "-C", usage = "Card pack files to use.", required = true, handler = StringArrayOptionHandler.class)
    private String[] cardPacks;
    @Option(name = "-z", usage = "Prefix to use for bot commands.", handler = CharOptionHandler.class)
    private char prefix = '!';
    @Option(name = "-N", usage = "NickServ password to identify with.", handler = StringOptionHandler.class)
    private String nickserv = "";
    @Option(name = "-d", usage = "Default packs to use on !start.", handler = StringArrayOptionHandler.class)
    private String[] defaultPacks = new String[0];
    @Option(name = "-k", usage = "Keep Cardcast packs loaded once they are downloaded?", handler = BooleanOptionHandler.class)
    private boolean keepCardcastPacks = false;
    @Option(name = "-D", usage = "Toggles debug mode.", handler = BooleanOptionHandler.class)
    private boolean debug = false;
    @Option(name = "-H", usage = "Hostname of the web server", handler = StringOptionHandler.class)
    private String webServerHostname = "0.0.0.0";
    @Option(name = "-w", usage = "Port of the web server", handler = IntOptionHandler.class)
    private int webServerPort = 9012;
    @Option(name = "-W", usage = "Run only the web server", handler = BooleanOptionHandler.class)
    private boolean runOnlyWebServer = false;
    @Option(name = "-X", usage = "Do not run the web server.", handler = BooleanOptionHandler.class)
    private boolean doNotRunWebServer = false;

    private TheHumanity(@NotNull final String[] args) {
        Preconditions.checkNotNull(args, "args was null");
        this.setUpLogger();
        this.parseArguments(args);
        if (!this.doNotRunWebServer) {
            HumanityConfiguration.setHumanity(this);
            this.gameServer = new GameServer(this.webServerHostname, this.webServerPort);
            if (this.runOnlyWebServer) {
                this.bot = null;
                this.pingRegistry = null;
                return;
            }
        } else {
            this.gameServer = null;
        }
        this.pingRegistry = PingRegistry.deserializeOrMakePingRegistry();
        // Schedule a repeatedly running saver task, just in case we're not shut down properly
        this.stpe.scheduleAtFixedRate(new SavePingRegistryTask(this.pingRegistry), 5L, 10L, TimeUnit.MINUTES);
        this.loadCardPacks();
        this.registerCommands();
        final ClientBuilder cb = Client.builder();
        cb
            .nick(this.nickname)
            .user(this.nickname)
            .name(this.nickname)
            .realName(this.nickname)
            .server(this.server)
            .server(this.serverPort)
            .secure(this.ssl)
            .listenInput(this.whoX);
        if (this.isDebugMode()) {
            cb.listenOutput(s -> System.out.println("output = " + s));
        }
        if (!this.nickserv.isEmpty()) {
            cb.after(client -> client.getAuthManager().addProtocol(new NickServ(client, this.nickname, this.nickserv)));
        }
        if (!this.serverPassword.isEmpty()) {
            cb.serverPassword(this.serverPassword);
        }
        this.bot = cb.build();
        this.bot.addChannel(this.channels);
        final EventManager em = this.bot.getEventManager();
        em.registerEventListener(new BaseListeners(this));
        em.registerEventListener(new GameListeners(this));
        this.addShutdownHook(); // The shutdown hook relies on everything being made.
    }

    public static void main(final String[] args) {
        new TheHumanity(args);
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(this)));
    }

    @Nullable
    private Manifest getManifest() {
        final Class<?> clazz = this.getClass();
        final String className = clazz.getSimpleName() + ".class";
        final String classPath = clazz.getResource(className).toString();
        final String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
        try {
            return new Manifest(new URL(manifestPath).openStream());
        } catch (final IOException ex) {
            return null;
        }
    }

    private void loadCardPacks() {
        new CardPackParser(this).parseCardPacks(this.cardPacks).forEach(this::addCardPack);
    }

    private void parseArguments(final String[] args) {
        final CmdLineParser clp = new CmdLineParser(this);
        try {
            clp.parseArgument(args);
        } catch (final CmdLineException e) {
            this.getLogger().info(e.getMessage());
            e.getParser().printUsage(System.out);
            System.exit(1);
        }
    }

    private void registerCommands() {
        Arrays.asList(
            new StartGameCommand(this),
            new JoinGameCommand(this),
            new PickCardCommand(this),
            new StopGameCommand(this),
            new LeaveGameCommand(this),
            new PacksCommand(this),
            new WhoCommand(this),
            new KickCommand(this),
            new SkipCommand(this),
            new HelpCommand(this),
            new CardsCommand(this),
            new RebootTheUniverseCommand(this),
            new CardCountsCommand(this),
            new ScoreCommand(this),
            new HostCommand(this),
            new GameCommand(this),
            new NeverHaveIEverCommand(this),
            new VersionCommand(this),
            new LoadCardPackCommand(this),
            new PingListCommand(this)
        ).forEach(this.getCommandHandler()::register);
    }

    private void setUpLogger() {
        final ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new Formatter() {
            @Override
            public String format(final LogRecord logRecord) {
                return "[" + logRecord.getLevel().getLocalizedName() + "] " + logRecord.getMessage() + "\n";
            }
        });
        this.getLogger().setUseParentHandlers(false);
        this.getLogger().addHandler(ch);
    }

    public void addCardPack(@NotNull final CardPack cp) {
        Preconditions.checkNotNull(cp, "cp was null");
        synchronized (this.loadedCardPacks) {
            this.loadedCardPacks.add(cp);
        }
    }

    public boolean areCardcastPacksKept() {
        return this.keepCardcastPacks;
    }

    /**
     * Gists the given contents under the given file name. This returns the URL to the Gist or a string of the following
     * format: "An error occurred: [error message]"
     * <p/>
     * The given ID is used for caching purposes. The cacheString should be a String that identifies the contents. If
     * cacheString changes, then the current cache for the given ID will be invalidated, and a new Gist will be made.
     *
     * @param key         Key of this cached gist
     * @param cacheString Identifier for the contents
     * @param fileName    Filename for the contents
     * @param contents    Contents of the Gist
     * @return URL of Gist or error message
     */
    @NotNull
    public String cachedGist(@NotNull final String key, @NotNull final String cacheString, @NotNull final String fileName, @NotNull final String contents) {
        // Ensure nothing is null
        Preconditions.checkNotNull(key, "key was null");
        Preconditions.checkNotNull(cacheString, "cacheString was null");
        Preconditions.checkNotNull(fileName, "fileName was null");
        Preconditions.checkNotNull(contents, "contents was null");
        // Get the pair of hashed cacheString and gist URL from the given key. If key is missing, this will be null
        final Pair<String, String> hashGist = this.gistCache.getIfPresent(key);
        // Compute the hash of the given cacheString. This has a cost, but saves on memory required to store long
        // strings
        final String hash = Hashing.md5().hashUnencodedChars(cacheString).toString();
        // If the cache didn't have anything or if the hashes are no longer equal
        if (hashGist == null || !hash.equals(hashGist.getLeft())) {
            // First, let's invalidate the key, since it is no longer valid
            this.gistCache.invalidate(key);
            // Now, let's gist and return the URL or error message
            return this.gist(fileName, contents);
        } else { // What if cache was not kill?
            // Return the cached gist URL
            return hashGist.getRight();
        }
    }

    @NotNull
    public Client getBot() {
        return this.bot;
    }

    @Nullable
    public CardPack getCardPack(@NotNull final String name) {
        Preconditions.checkNotNull(name, "name was null");
        synchronized (this.loadedCardPacks) {
            return this.loadedCardPacks.stream().filter(cp -> cp.getName().equals(name)).findFirst().orElse(null);
        }
    }

    @NotNull
    public List<CardPack> getCardPacksFromArguments(final String[] args) {
        final List<CardPack> packs = CardPackParser.getListOfCardPackNames(args, this.getDefaultPacks()).stream()
            .map(this::getOrDownloadCardPack)
            .filter(cp -> cp != null)
            .collect(Collectors.toList());
        if (this.areCardcastPacksKept()) {
            packs.stream()
                .filter(pack -> pack instanceof CardcastCardPack)
                .filter(pack -> !this.getLoadedCardPacks().contains(pack))
                .forEach(this::addCardPack);
        }
        return packs;
    }

    @NotNull
    public CommandHandler getCommandHandler() {
        return this.ch;
    }

    @NotNull
    public String[] getDefaultPacks() {
        return this.defaultPacks.clone();
    }

    @Nullable
    public Game getGameFor(@NotNull final User u) {
        Preconditions.checkNotNull(u, "u was null");
        return this.getGames().values().stream()
            .filter(g -> g.hasPlayer(u.getNick()))
            .findFirst()
            .orElse(null);
    }

    @Nullable
    public Game getGameFor(@NotNull final Channel c) {
        Preconditions.checkNotNull(c, "c was null");
        return this.getGames().get(c);
    }

    @Nullable
    public GameServer getGameServer() {
        return this.gameServer;
    }

    @NotNull
    public Map<Channel, Game> getGames() {
        return this.games;
    }

    public History getHistory() {
        return this.history;
    }

    @NotNull
    public List<CardPack> getLoadedCardPacks() {
        synchronized (this.loadedCardPacks) {
            return this.loadedCardPacks;
        }
    }

    @NotNull
    public Logger getLogger() {
        return this.l;
    }

    @Nullable
    public CardPack getOrDownloadCardPack(@NotNull final String name) {
        Preconditions.checkNotNull(name, "name was null");
        final CardPack cp = this.getCardPack(name);
        if (cp != null) return cp;
        if (!name.toLowerCase().startsWith("cc:")) return null;
        return new CardcastFetcher(name.substring(3)).getCardPack();
    }

    public PingRegistry getPingRegistry() {
        return this.pingRegistry;
    }

    public char getPrefix() {
        return this.prefix;
    }

    @NotNull
    public ScheduledThreadPoolExecutor getThreadPool() {
        return this.stpe;
    }

    @NotNull
    public String getVersion() {
        final Manifest mf = this.getManifest();
        if (mf == null) return "Error: null Manifest";
        final Attributes a = mf.getAttributes("Version-Info");
        if (a == null) return "Error: No Version-Info";
        return String.format(
            "%s %s (%s)",
            a.getValue("Project-Name"),
            a.getValue("Project-Version"),
            a.getValue("Git-Describe")
        );
    }

    public WhoX getWhoX() {
        return this.whoX;
    }

    /**
     * Gists the given contents under the given file name. This returns the URL to the Gist or a string of the following
     * format: "An error occurred: [error message]"
     *
     * @param fileName Filename for the contents
     * @param contents Contents of the Gist
     * @return URL of Gist or error message
     */
    @NotNull
    public String gist(@NotNull final String fileName, @NotNull final String contents) {
        // Ensure nothing is null
        Preconditions.checkNotNull(fileName, "fileName was null");
        Preconditions.checkNotNull(contents, "contents was null");
        // Let's gist the contents using the given fileName.
        // Make a StringWriter to turn this JSON into a String, easily
        final StringWriter sw = new StringWriter();
        final JSONWriter jw = new JSONWriter(sw);
        // Create the gist object for sending to the API
        jw.object().key("files")
            .object().key(fileName)
            .object().key("content").value(contents)
            .endObject().endObject().endObject();
        try {
            // POST the gist object to the appropriate API URL and grab the response as JSON
            final HttpResponse<JsonNode> response = Unirest
                .post("https://api.github.com/gists")
                .body(sw.toString())
                .asJson();
            // This should be the URL at which the gist can be accessed. Will throw exception if key isn't present
            // Finally, let's give the caller the URL to the gist
            return response.getBody().getObject().getString("html_url");
        } catch (final UnirestException | JSONException ex) {
            return "An error occurred: " + ex.getMessage();
        }
    }

    public boolean hasChannelMode(@NotNull final Channel c, @NotNull final User u, final char mode) {
        Preconditions.checkNotNull(c, "Channel was null");
        Preconditions.checkNotNull(u, "User was null");
        Optional<Set<ChannelUserMode>> set = c.getUserModes(u.getNick());
        return set.isPresent() && set.get().stream().anyMatch(m -> m.getChar() == mode);
    }

    public boolean isDebugMode() {
        return this.debug;
    }

    @Nullable
    public CardPack parseOrDownloadCardPack(@NotNull final String name) {
        Preconditions.checkNotNull(name, "name was null");
        if (name.toLowerCase().startsWith("cc:")) {
            final String id = name.substring(3).toUpperCase();
            CardcastFetcher.invalidateCacheFor(id);
            return new CardcastFetcher(id).getCardPack();
        }
        return new CardPackParser(this).parseCardPack(name);
    }

    public void removeCardPack(@NotNull final CardPack cp) {
        Preconditions.checkNotNull(cp, "cp was null");
        synchronized (this.loadedCardPacks) {
            this.loadedCardPacks.remove(cp);
        }
    }

    public boolean usersMatch(@NotNull final User u, @NotNull final User u2) {
        Preconditions.checkNotNull(u, "User was null");
        Preconditions.checkNotNull(u2, "Second user was null");
        return u.getNick().equals(u2.getNick());
    }
}
