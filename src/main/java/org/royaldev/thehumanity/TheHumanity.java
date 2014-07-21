package org.royaldev.thehumanity;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.CharOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.kohsuke.args4j.spi.StringOptionHandler;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.royaldev.thehumanity.cards.Card.BlackCard;
import org.royaldev.thehumanity.cards.Card.WhiteCard;
import org.royaldev.thehumanity.cards.CardPack;
import org.royaldev.thehumanity.commands.impl.JoinGameCommand;
import org.royaldev.thehumanity.commands.impl.KickCommand;
import org.royaldev.thehumanity.commands.impl.LeaveGameCommand;
import org.royaldev.thehumanity.commands.impl.PacksCommand;
import org.royaldev.thehumanity.commands.impl.PickCardCommand;
import org.royaldev.thehumanity.commands.impl.SkipCommand;
import org.royaldev.thehumanity.commands.impl.StartGameCommand;
import org.royaldev.thehumanity.commands.impl.StopGameCommand;
import org.royaldev.thehumanity.commands.impl.WhoCommand;
import org.royaldev.thehumanity.handlers.CommandHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@SuppressWarnings("MismatchedReadAndWriteOfArray")
public class TheHumanity {

    private final List<CardPack> loadedCardPacks = Collections.synchronizedList(new ArrayList<CardPack>());
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

    private Logger l = Logger.getLogger("org.royaldev.thehumanity");
    private ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);

    private TheHumanity(final String[] args) {
        this.setUpLogger();
        this.parseArguments(args);
        this.loadCardPacks();
        this.registerCommands();
        final Configuration.Builder<PircBotX> cb = new Configuration.Builder<>();
        cb.setAutoNickChange(true).setAutoReconnect(true).setLogin(this.nickname).setMessageDelay(0L).setName(this.nickname).setRealName(this.nickname).setServer(this.server, this.serverPort);
        for (final String channel : this.channels) cb.addAutoJoinChannel(channel);
        cb.addListener(new BaseListeners(this)).addListener(new GameListeners(this));
        if (!this.nickserv.isEmpty()) cb.setNickservPassword(this.nickserv);
        if (!this.serverPassword.isEmpty()) cb.setServerPassword(this.serverPassword);
        this.bot = new PircBotX(cb.buildConfiguration());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bot.startBot();
                } catch (IOException | IrcException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        new TheHumanity(args);
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
    }

    private void parseArguments(final String[] args) {
        final CmdLineParser clp = new CmdLineParser(this);
        try {
            clp.parseArgument(args);
        } catch (CmdLineException e) {
            this.getLogger().info(e.getMessage());
            e.getParser().printUsage(System.out);
            System.exit(1);
        }
    }

    private void setUpLogger() {
        final ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord logRecord) {
                return "[" + logRecord.getLevel().getLocalizedName() + "] " + logRecord.getMessage() + "\n";
            }
        });
        this.getLogger().setUseParentHandlers(false);
        this.getLogger().addHandler(ch);
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
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(f));
                String line;
                boolean isBlack = false;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.equalsIgnoreCase("___WHITE___")) {
                        isBlack = false;
                        continue;
                    } else if (line.equalsIgnoreCase("___BLACK___")) {
                        isBlack = true;
                        continue;
                    }
                    if (line.isEmpty()) continue;
                    if (isBlack) cp.addCard(new BlackCard(cp, line));
                    else cp.addCard(new WhiteCard(cp, line));
                }
            } catch (IOException ex) {
                this.getLogger().warning(ex.getMessage());
                continue;
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            this.addCardPack(cp);
        }
    }

    public Logger getLogger() {
        return this.l;
    }

    public void addCardPack(final CardPack cp) {
        synchronized (this.loadedCardPacks) {
            this.loadedCardPacks.add(cp);
        }
    }

    public CardPack getCardPack(final String name) {
        synchronized (this.loadedCardPacks) {
            for (final CardPack cp : this.loadedCardPacks) {
                if (cp.getName().equalsIgnoreCase(name)) return cp;
            }
        }
        return null;
    }

    public List<CardPack> getLoadedCardPacks() {
        synchronized (this.loadedCardPacks) {
            return this.loadedCardPacks;
        }
    }

    public Game getGameFor(String nickname) {
        for (final Game game : this.getGames().values()) {
            if (!game.hasUser(nickname)) continue;
            return game;
        }
        return null;
    }

    public Game getGameFor(User u) {
        for (final Game game : this.getGames().values()) {
            if (!game.hasUser(u)) continue;
            return game;
        }
        return null;
    }

    public boolean usersMatch(User u, User u2) {
        return u.getNick().equals(u2.getNick()); // because PircBotX doesn't know how to use UUIDs because OOHHHHH NO
    }

    public Game getGameFor(Channel c) {
        return this.getGames().get(c);
    }

    public char getPrefix() {
        return this.prefix;
    }

    public PircBotX getBot() {
        return this.bot;
    }

    public CommandHandler getCommandHandler() {
        return this.ch;
    }

    public Map<Channel, Game> getGames() {
        return this.games;
    }

    public ScheduledThreadPoolExecutor getThreadPool() {
        return this.stpe;
    }
}
