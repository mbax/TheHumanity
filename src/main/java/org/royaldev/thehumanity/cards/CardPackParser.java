package org.royaldev.thehumanity.cards;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.cards.types.BlackCard;
import org.royaldev.thehumanity.cards.types.WhiteCard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A parser for CardPack files.
 */
public class CardPackParser {

    private static final Pattern cardPackArgumentPattern = Pattern.compile("(\".+?(?<!\\\\)\"|'.+?(?<!\\\\)'|\\S+)");
    @NotNull
    private final TheHumanity humanity;

    public CardPackParser(@NotNull final TheHumanity humanity) {
        Preconditions.checkNotNull(humanity, "humanity was null");
        this.humanity = humanity;
    }

    /**
     * Takes arguments, usually passed to a command, for a list of CardPacks, then converts them into a list of names.
     * This supports quoted card packs to allow spaces and quotes.
     * <p>See <a href="http://rubular.com/r/XYxY0EFyLt">this</a> regex.
     *
     * @param args Array of arguments
     * @return List of names
     */
    @NotNull
    public static List<String> getListOfCardPackNames(final String[] args) {
        final List<String> names = Lists.newArrayList();
        final String joined = Joiner.on(' ').join(args);
        final Matcher m = CardPackParser.cardPackArgumentPattern.matcher(joined);
        while (m.find()) {
            String name = m.group(1).replaceAll("(\\\\(?!\\s))", "");
            if (name.length() > 1 && (name.startsWith("\"") && name.endsWith("\"") || name.startsWith("'") && name.endsWith("'"))) {
                name = name.substring(1, name.length() - 1);
            }
            names.add(name);
        }
        return names;
    }

    /**
     * Parses one CardPack given the name of the file that contains it. If there is any IOException while processing, or
     * if the file cannot be read, null will be returned.
     *
     * @param name Name of the file the CardPack is contained in
     * @return CardPack or null
     */
    @Nullable
    public CardPack parseCardPack(@NotNull final String name) {
        Preconditions.checkNotNull(name, "name was null");
        final File f = new File("cardpacks", name);
        if (!f.exists() || !f.isFile()) {
            this.humanity.getLogger().warning(f.getName() + " does not exist.");
            return null;
        }
        if (!f.canRead()) {
            this.humanity.getLogger().warning("Cannot read " + f.getName() + ".");
            return null;
        }
        final CardPack cp = new CardPack(CardPack.getNameFromFileName(f.getName()));
        try (final BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            ParseStage ps = ParseStage.METADATA;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                ps = ps.parse(cp, line);
            }
        } catch (final IOException ex) {
            this.humanity.getLogger().warning(ex.getMessage());
            return null;
        }
        return cp;
    }

    /**
     * Returns a Collection of CardPacks given an array of their file names. This calls the {@link #parseCardPack}
     * method, but all null results are filtered out of the returned Collection.
     *
     * @param names Array of names of files CardPacks are contained in
     * @return Collection not containing null
     */
    @NotNull
    public Collection<CardPack> parseCardPacks(@NotNull final String[] names) {
        Preconditions.checkNotNull(names, "names was null");
        return Arrays.stream(names).map(this::parseCardPack).filter(cp -> cp != null).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Stages of parsing for {@link CardPackParser}.
     */
    private enum ParseStage {
        /**
         * Parsing the metadata section.
         */
        METADATA("___METADATA___") {
            @NotNull
            @Override
            ParseStage parseInternal(@NotNull final CardPack cp, @NotNull final String line) {
                Preconditions.checkNotNull(cp, "cp was null");
                Preconditions.checkNotNull(line, "line was null");
                final String[] parts = line.split("\\s*:\\s*");
                if (parts.length < 2) return this;
                final String key = parts[0];
                final String value = Joiner.on(' ').join(Arrays.copyOfRange(parts, 1, parts.length));
                switch (key.toLowerCase()) {
                    case "description":
                        cp.setDescription(value);
                        break;
                    case "author":
                        cp.setAuthor(value);
                        break;
                }
                return this;
            }
        },
        /**
         * Parsing the black cards section.
         */
        BLACK_CARDS("___BLACK___") {
            @NotNull
            @Override
            ParseStage parseInternal(@NotNull final CardPack cp, @NotNull final String line) {
                Preconditions.checkNotNull(cp, "cp was null");
                Preconditions.checkNotNull(line, "line was null");
                cp.addCard(new BlackCard(cp, line));
                return this;
            }
        },
        /**
         * Parsing the white cards section.
         */
        WHITE_CARDS("___WHITE___") {
            @NotNull
            @Override
            ParseStage parseInternal(@NotNull final CardPack cp, @NotNull final String line) {
                Preconditions.checkNotNull(cp, "cp was null");
                Preconditions.checkNotNull(line, "line was null");
                cp.addCard(new WhiteCard(cp, line));
                return this;
            }
        };

        private final String header;

        ParseStage(final String header) {
            this.header = header;
        }

        /**
         * Gets the matching ParseStage from a given header line. If there is no matching header, null will be returned.
         *
         * @param line Header line to match
         * @return ParseType or null if no matching header
         */
        @Nullable
        static ParseStage getHeaderType(@NotNull final String line) {
            Preconditions.checkNotNull(line, "line was null");
            return Arrays.stream(ParseStage.values())
                .filter(ps -> ps.getHeader().equals(line))
                .findFirst()
                .orElse(null);
        }

        @NotNull
        abstract ParseStage parseInternal(@NotNull final CardPack cp, @NotNull final String line);

        /**
         * Gets the header of this section (e.g. "___BLACK___"). The header declares the start of a new section in a
         * CardPack file.
         *
         * @return Header
         */
        @NotNull
        String getHeader() {
            return this.header;
        }

        /**
         * Parses one line into the given CardPack. This will return the next ParseStage to use for correct parsing.
         *
         * @param cp   CardPack to parse for
         * @param line Line to parse
         * @return The next ParseStage, never null
         */
        @NotNull
        ParseStage parse(@NotNull final CardPack cp, @NotNull final String line) {
            Preconditions.checkNotNull(cp, "cp was null");
            Preconditions.checkNotNull(line, "line was null");
            final ParseStage headerType = ParseStage.getHeaderType(line);
            if (headerType != null) {
                return headerType;
            }
            return this.parseInternal(cp, line);
        }
    }

}
