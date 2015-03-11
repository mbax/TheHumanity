package org.royaldev.thehumanity;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.CharOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.kohsuke.args4j.spi.StringOptionHandler;
import org.pircbotx.Channel;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;
import org.royaldev.thehumanity.commands.impl.CardCountsCommand;
import org.royaldev.thehumanity.commands.impl.CardsCommand;
import org.royaldev.thehumanity.commands.impl.HelpCommand;
import org.royaldev.thehumanity.commands.impl.JoinGameCommand;
import org.royaldev.thehumanity.commands.impl.KickCommand;
import org.royaldev.thehumanity.commands.impl.LeaveGameCommand;
import org.royaldev.thehumanity.commands.impl.PacksCommand;
import org.royaldev.thehumanity.commands.impl.PickCardCommand;
import org.royaldev.thehumanity.commands.impl.RebootTheUniverseCommand;
import org.royaldev.thehumanity.commands.impl.SkipCommand;
import org.royaldev.thehumanity.commands.impl.StartGameCommand;
import org.royaldev.thehumanity.commands.impl.StopGameCommand;
import org.royaldev.thehumanity.commands.impl.WhoCommand;
import org.royaldev.thehumanity.handlers.CommandHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@SuppressWarnings({"MismatchedReadAndWriteOfArray", "FieldMayBeFinal"})
public class TheHumanity {

    private final List<CardPack> loadedCardPacks = Collections.synchronizedList(new ArrayList<>());
    private final PircBotX bot;
    private final CommandHandler ch = new CommandHandler();
    private final Map<Channel, Game> games = new HashMap<>();

    @Option(name = "-c", usage = "Channels to join.", required = true, handler = StringArrayOptionHandler.class)
    private String[] channels;
    @Option(name = "-s", usage = "Server to connect to.", required = true, handler = StringOptionHandler.class)
    private String server;
    @Option(name = "-p", usage = "Server port.", handler = IntOptionHandler.class)
    private int serverPort = 6667;
    @Option(name = "-P", usage = "Server password.", handler = StringOptionHandler.class)
    private String serverPassword = "";
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

    private Logger l = Logger.getLogger("org.royaldev.thehumanity");
    private ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);

    private TheHumanity(final String[] args) {
        this.setUpLogger();
        this.parseArguments(args);
        this.loadCardPacks();
        this.registerCommands();
        final Builder<PircBotX> cb = new Builder<>();
        cb.setAutoNickChange(true).setAutoReconnect(true).setLogin(this.nickname).setMessageDelay(0L).setName(this.nickname).setRealName(this.nickname).setServer(this.server, this.serverPort).setEncoding(Charset.forName("UTF-8"));
        Arrays.stream(this.channels).forEach(cb::addAutoJoinChannel);
        cb.addListener(new BaseListeners(this)).addListener(new GameListeners(this));
        if (!this.nickserv.isEmpty()) cb.setNickservPassword(this.nickserv);
        if (!this.serverPassword.isEmpty()) cb.setServerPassword(this.serverPassword);
        this.bot = new PircBotX(cb.buildConfiguration());
        new Thread(() -> {
            try {
                TheHumanity.this.bot.startBot();
            } catch (final IOException | IrcException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        new TheHumanity(args);
    }

    private void loadCardPacks() {
        for (final String cardPack : this.cardPacks) {
            final File f = new File("cardpacks", cardPack);
            if (!f.exists() || !f.isFile()) {
                this.getLogger().warning(f.getName() + " does not exist.");
                continue;
            }
            if (!f.canRead()) {
                this.getLogger().warning("Cannot read " + f.getName() + ".");
                continue;
            }
            final CardPack cp = new CardPack(CardPack.getNameFromFileName(f.getName()));
            try (final BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                boolean isBlack = false;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    if ("___WHITE___".equalsIgnoreCase(line)) {
                        isBlack = false;
                        continue;
                    } else if ("___BLACK___".equalsIgnoreCase(line)) {
                        isBlack = true;
                        continue;
                    }
                    if (isBlack) cp.addCard(new BlackCard(cp, line));
                    else cp.addCard(new WhiteCard(cp, line));
                }
            } catch (final IOException ex) {
                this.getLogger().warning(ex.getMessage());
                continue;
            }
            this.addCardPack(cp);
        }
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

    public PircBotX getBot() {
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

    public boolean usersMatch(final User u, final User u2) {
        return u.getNick().equals(u2.getNick()); // because PircBotX doesn't know how to use UUIDs because OOHHHHH NO
    }
}
