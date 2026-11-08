package com.jwtoffline.workspace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jwtoffline.core.JwtException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Workspace that is written to a JSON file after every change, so saved tokens survive closing the app (FR-22).
 * Only tokens and labels are stored. Secrets and keys typed on other screens never reach this class (NFR-1).
 */
public class FileTokenRepository extends InMemoryTokenRepository {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Path file;
    private String loadWarning;

    /**
     * @param file         where the workspace is kept
     * @param seedSamples  add the demo tokens when the file does not exist yet (first launch)
     */
    public FileTokenRepository(Path file, boolean seedSamples, Clock clock) {
        super(clock);
        this.file = file;
        if (Files.exists(file)) {
            load();
        } else if (seedSamples) {
            addSamples();
        }
    }

    public FileTokenRepository(Path file) {
        this(file, false, Clock.systemUTC());
    }

    /** The workspace in the user's home folder, with sample tokens on first launch. */
    public static FileTokenRepository openDefault() {
        Path file = Path.of(System.getProperty("user.home"), ".jwtoffline", "workspace.json");
        return new FileTokenRepository(file, true, Clock.systemUTC());
    }

    public Path file() {
        return file;
    }

    /** A message for the user if the file could not be read when the app started. */
    public Optional<String> loadWarning() {
        return Optional.ofNullable(loadWarning);
    }

    @Override
    protected void changed() {
        List<Entry> entries = new ArrayList<>();
        for (SavedToken t : findAll()) {
            entries.add(new Entry(t.id(), t.label(), t.token(), t.savedAt().toString()));
        }
        try {
            Path parent = file.toAbsolutePath().getParent();
            Files.createDirectories(parent);
            Path tmp = Files.createTempFile(parent, "workspace", ".tmp");
            Files.writeString(tmp, GSON.toJson(entries), StandardCharsets.UTF_8);
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new JwtException("Could not save the workspace to " + file + ": " + ex.getMessage(), ex);
        }
    }

    private void load() {
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            List<Entry> entries = GSON.fromJson(json, new TypeToken<List<Entry>>() { }.getType());
            List<SavedToken> tokens = new ArrayList<>();
            if (entries != null) {
                for (Entry e : entries) {
                    tokens.add(new SavedToken(e.id, e.label, e.token, Instant.parse(e.savedAt)));
                }
            }
            restore(tokens);
        } catch (IOException | JsonParseException | DateTimeParseException | NullPointerException ex) {
            keepCorruptCopy();
        }
    }

    private void keepCorruptCopy() {
        Path backup = file.resolveSibling(file.getFileName() + ".corrupt");
        try {
            Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
            loadWarning = "The workspace file could not be read, so the workspace started empty. "
                    + "A copy of the old file was kept at " + backup;
        } catch (IOException ex) {
            loadWarning = "The workspace file could not be read, so the workspace started empty.";
        }
    }

    /** One row of the JSON file. Instant is kept as ISO-8601 text so the file is easy to read. */
    private static final class Entry {
        String id;
        String label;
        String token;
        String savedAt;

        Entry(String id, String label, String token, String savedAt) {
            this.id = id;
            this.label = label;
            this.token = token;
            this.savedAt = savedAt;
        }
    }
}
