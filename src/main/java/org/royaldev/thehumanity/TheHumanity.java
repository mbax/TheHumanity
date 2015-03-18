package org.royaldev.thehumanity;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONWriter;
import org.kitteh.irc.client.library.AuthType;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.ClientBuilder;
import org.kitteh.irc.client.library.EventManager;
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
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.commands.impl.CardCountsCommand;
import org.royaldev.thehumanity.commands.impl.CardsCommand;
import org.royaldev.thehumanity.commands.impl.HelpCommand;
import org.royaldev.thehumanity.commands.impl.HostCommand;
import org.royaldev.thehumanity.commands.impl.JoinGameCommand;
import org.royaldev.thehumanity.commands.impl.KickCommand;
import org.royaldev.thehumanity.commands.impl.LeaveGameCommand;
import org.royaldev.thehumanity.commands.impl.PacksCommand;
import org.royaldev.thehumanity.commands.impl.PickCardCommand;
import org.royaldev.thehumanity.commands.impl.RebootTheUniverseCommand;
import org.royaldev.thehumanity.commands.impl.ScoreCommand;
import org.royaldev.thehumanity.commands.impl.SkipCommand;
import org.royaldev.thehumanity.commands.impl.StartGameCommand;
import org.royaldev.thehumanity.commands.impl.StopGameCommand;
import org.royaldev.thehumanity.commands.impl.WhoCommand;
import org.royaldev.thehumanity.commands.impl.game.GameCommand;
import org.royaldev.thehumanity.handlers.CommandHandler;
import org.royaldev.thehumanity.util.Pair;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@SuppressWarnings({"MismatchedReadAndWriteOfArray", "FieldMayBeFinal"})
public class TheHumanity {

    private final List<CardPack> loadedCardPacks = Collections.synchronizedList(new ArrayList<>());
    private final Client bot;
    private final CommandHandler ch = new CommandHandler();
    private final Map<Channel, Game> games = new HashMap<>();
    private final Map<String, Pair<String, String>> gistCache = new HashMap<>();
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
    private Logger l = Logger.getLogger("org.royaldev.thehumanity");
    private ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);

    private TheHumanity(final String[] args) {
        this.setUpLogger();
        this.parseArguments(args);
        this.loadCardPacks();
        this.registerCommands();
        final ClientBuilder cb = new ClientBuilder();
        cb.nick(this.nickname).user(this.nickname).name(this.nickname).realName(this.nickname).server(this.server).server(this.serverPort);
        if (!this.nickserv.isEmpty()) cb.auth(AuthType.NICKSERV, this.nickname, this.nickserv);
        if (!this.serverPassword.isEmpty()) cb.serverPassword(this.serverPassword);
        this.bot = cb.build();
        this.bot.addChannel(this.channels);
        final EventManager em = this.bot.getEventManager();
        em.registerEventListener(new BaseListeners(this));
        em.registerEventListener(new GameListeners(this));
    }

    public static void main(final String[] args) {
        new TheHumanity(args);
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
        this.getCommandHandler().register(new StartGameCommand(this));
        this.getCommandHandler().register(new JoinGameCommand(this));
        this.getCommandHandler().register(new PickCardCommand(this));
        this.getCommandHandler().register(new StopGameCommand(this));
        this.getCommandHandler().register(new LeaveGameCommand(this));
        this.getCommandHandler().register(new PacksCommand(this));
        this.getCommandHandler().register(new WhoCommand(this));
        this.getCommandHandler().register(new KickCommand(this));
        this.getCommandHandler().register(new SkipCommand(this));
        this.getCommandHandler().register(new HelpCommand(this));
        this.getCommandHandler().register(new CardsCommand(this));
        this.getCommandHandler().register(new RebootTheUniverseCommand(this));
        this.getCommandHandler().register(new CardCountsCommand(this));
        this.getCommandHandler().register(new ScoreCommand(this));
        this.getCommandHandler().register(new HostCommand(this));
        this.getCommandHandler().register(new GameCommand(this));
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

    public void addCardPack(final CardPack cp) {
        synchronized (this.loadedCardPacks) {
            this.loadedCardPacks.add(cp);
        }
    }

    public boolean areCardcastPacksKept() {
        return this.keepCardcastPacks;
    }

    public Client getBot() {
        return this.bot;
    }

    public CardPack getCardPack(final String name) {
        synchronized (this.loadedCardPacks) {
            return this.loadedCardPacks.stream().filter(cp -> cp.getName().equals(name)).findFirst().orElse(null);
        }
    }

    public CommandHandler getCommandHandler() {
        return this.ch;
    }

    public String[] getDefaultPacks() {
        return this.defaultPacks.clone();
    }

    public Game getGameFor(final User u) {
        return this.getGames().values().stream().filter(g -> g.hasPlayer(u.getNick())).findFirst().orElse(null);
    }

    public Game getGameFor(final Channel c) {
        return this.getGames().get(c);
    }

    public Map<Channel, Game> getGames() {
        return this.games;
    }

    public List<CardPack> getLoadedCardPacks() {
        synchronized (this.loadedCardPacks) {
            return this.loadedCardPacks;
        }
    }

    public Logger getLogger() {
        return this.l;
    }

    public char getPrefix() {
        return this.prefix;
    }

    public ScheduledThreadPoolExecutor getThreadPool() {
        return this.stpe;
    }

    /**
     * Gists the given contents under the given file name. This returns the URL to the Gist or a string of the following
     * format: "An error occurred: [error message]"
     * <p/>
     * The given ID is used for caching purposes. The cacheString should be a String that identifies the contents. If
     * cacheString changes, then the current cache for the given ID will be invalidated, and a new Gist will be made.
     *
     * @param id          ID of this cached gist
     * @param cacheString Identifier for the contents
     * @param fileName    Filename for the contents
     * @param contents    Contents of the Gist
     * @return URL of Gist or error message
     */
    public String gist(final String id, final String cacheString, final String fileName, final String contents) {
        final Pair<String, String> hashGist = this.gistCache.get(id);
        final String hash = DigestUtils.md5Hex(cacheString);
        if (hashGist == null || !hash.equals(hashGist.getLeft())) {
            final StringWriter sw = new StringWriter();
            final JSONWriter jw = new JSONWriter(sw);
            jw.object().key("files")
                .object().key(fileName)
                .object().key("content").value(contents)
                .endObject().endObject().endObject();
            try {
                final HttpResponse<JsonNode> response = Unirest
                    .post("https://api.github.com/gists")
                    .body(sw.toString())
                    .asJson();
                final String gist = response.getBody().getObject().getString("html_url");
                this.gistCache.put(id, new Pair<>(hash, gist));
                return gist;
            } catch (final UnirestException ex) {
                return "An error occurred: " + ex.getMessage();
            }
        } else {
            return hashGist.getRight();
        }
    }

    public boolean hasChannelMode(final Channel c, final User u, final char mode) {
        final Map<User, Set<ChannelUserMode>> users = c.getUsers();
        return users.containsKey(u) && users.get(u).stream().anyMatch(m -> m.getMode() == mode);
    }

    public boolean usersMatch(final User u, final User u2) {
        return u.getNick().equals(u2.getNick());
    }
}
