package org.royaldev.thehumanity.history;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.royaldev.thehumanity.TheHumanity;
import org.royaldev.thehumanity.game.GameSnapshot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class History {

    private final TheHumanity humanity;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Cache<String, GameSnapshot> cache = CacheBuilder.newBuilder()
        .expireAfterAccess(1L, TimeUnit.HOURS)
        .build();
    private final Object saveLock = new Object();

    public History(@NotNull final TheHumanity humanity) {
        Preconditions.checkNotNull(humanity, "humanity was null");
        this.humanity = humanity;
        this.createHistorySchema();
    }

    private boolean createFile(@NotNull final File file) {
        Preconditions.checkNotNull(file, "file was null");
        try {
            return !file.exists() && file.getParentFile().mkdirs() && file.createNewFile();
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean createFolder(@NotNull final File folder) {
        Preconditions.checkNotNull(folder, "folder was null");
        return !folder.exists() && folder.mkdirs();
    }

    private void createHistorySchema() {
        this.createFolder(this.getHistoryFolder());
    }

    @Nullable
    private String loadGameSnapshotJSON(@NotNull final String channel, final int number) {
        Preconditions.checkNotNull(channel, "channel was null");
        final File gameLocation = this.getGameSnapshotFile(channel, number);
        if (!gameLocation.exists()) return null;
        final List<String> lines;
        try {
            lines = Files.readAllLines(gameLocation.toPath());
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        return Joiner.on('\n').join(lines);
    }

    public int[] getAllGameNumbers(@NotNull final String channel) {
        Preconditions.checkNotNull(channel, "channel was null");
        final File channelFolder = this.getChannelFolder(channel);
        if (!channelFolder.exists()) return new int[0];
        if (!channelFolder.isDirectory()) throw new IllegalStateException("channel folder is not a directory");
        return Arrays.stream(channelFolder.list()).mapToInt(name -> {
            try {
                return Integer.parseInt(name.split("\\.")[0]);
            } catch (final NumberFormatException ex) {
                return -1;
            }
        }).filter(number -> number > 0).toArray();
    }

    @NotNull
    public File getChannelFolder(@NotNull final String channel) {
        Preconditions.checkNotNull(channel, "channel was null");
        return new File(this.getHistoryFolder(), channel.toLowerCase());
    }

    @NotNull
    public File getGameSnapshotFile(@NotNull final String channel, final int number) {
        Preconditions.checkNotNull(channel, "channel was null");
        return new File(this.getChannelFolder(channel), number + ".json");
    }

    public File getHistoryFolder() {
        return new File("history");
    }

    public int getLastGameSnapshotNumber(@NotNull final String channel) {
        Preconditions.checkNotNull(channel, "channel was null");
        return Arrays.stream(this.getAllGameNumbers(channel)).max().orElse(0);
    }

    /**
     * Loads a GameSnapshot from the disk. If the game could not be located, this will return null.
     * <p>Snapshots are cached for an hour before they are invalidated and loaded from the disk again.
     * <p>If the number given is 0, the current game will be retrieved and a snapshot will be returned from that game.
     * If there is no game, null will be returned. If the number is negative, an IllegalArgumentException will be
     * thrown.
     *
     * @param channel Channel to load game from. Ex: "#CAHdev"
     * @param number  Number (not index) of the game to load. Ex: 5
     * @return GameSnapshot or null
     * @throws IllegalArgumentException If {@code number} is negative
     */
    @Nullable
    public GameSnapshot loadGameSnapshot(@NotNull final String channel, final int number) {
        Preconditions.checkNotNull(channel, "channel was null");
        if (number < 0) {
            throw new IllegalArgumentException("Game number was negative");
        }
        final String cacheKey = channel.toLowerCase() + ":" + number;
        final GameSnapshot cached = this.cache.getIfPresent(cacheKey);
        if (cached != null) {
            return cached;
        }
        final String json = this.loadGameSnapshotJSON(channel, number);
        if (json == null) {
            return null;
        }
        try {
            final GameSnapshot gs = this.objectMapper.readValue(json, GameSnapshot.class);
            this.cache.put(cacheKey, gs);
            return gs;
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void saveGameSnapshot(@NotNull final GameSnapshot gameSnapshot) {
        synchronized (this.saveLock) {
            Preconditions.checkNotNull(gameSnapshot, "gameSnapshot was null");
            final String channel = gameSnapshot.getChannel();
            final File gameLocation = this.getGameSnapshotFile(channel, this.getLastGameSnapshotNumber(channel) + 1);
            this.createFile(gameLocation);
            try {
                Files.write(gameLocation.toPath(), gameSnapshot.toJSON().getBytes(StandardCharsets.UTF_8));
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
