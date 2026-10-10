package com.jwtoffline.workspace;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/** Stub repository for the prototype. Data lives only while the app is open. */
public class InMemoryTokenRepository implements TokenRepository {

    private final Map<String, SavedToken> tokens = new LinkedHashMap<>();
    private final Clock clock;

    public InMemoryTokenRepository() {
        this(Clock.systemUTC());
    }

    public InMemoryTokenRepository(Clock clock) {
        this.clock = clock;
    }

    /** Repository pre loaded with sample tokens so the workspace screen has something to show. */
    public static InMemoryTokenRepository withSampleData() {
        InMemoryTokenRepository repo = new InMemoryTokenRepository();
        for (String[] sample : SampleTokens.ALL) {
            repo.save(sample[0], sample[1]);
        }
        return repo;
    }

    @Override
    public SavedToken save(String label, String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token is empty");
        }
        String cleanLabel = label == null || label.isBlank() ? "Untitled token" : label.trim();
        SavedToken saved = new SavedToken(UUID.randomUUID().toString(), cleanLabel, token.trim(), clock.instant());
        tokens.put(saved.id(), saved);
        return saved;
    }

    @Override
    public List<SavedToken> findAll() {
        List<SavedToken> list = new ArrayList<>(tokens.values());
        list.sort(Comparator.comparing(SavedToken::savedAt).reversed());
        return list;
    }

    @Override
    public Optional<SavedToken> findById(String id) {
        return Optional.ofNullable(tokens.get(id));
    }

    @Override
    public List<SavedToken> search(String text) {
        if (text == null || text.isBlank()) {
            return findAll();
        }
        String needle = text.toLowerCase(Locale.ROOT);
        return findAll().stream()
                .filter(t -> t.label().toLowerCase(Locale.ROOT).contains(needle))
                .collect(Collectors.toList());
    }

    @Override
    public boolean rename(String id, String newLabel) {
        SavedToken existing = tokens.get(id);
        if (existing == null || newLabel == null || newLabel.isBlank()) {
            return false;
        }
        tokens.put(id, existing.withLabel(newLabel.trim()));
        return true;
    }

    @Override
    public boolean delete(String id) {
        return tokens.remove(id) != null;
    }
}
