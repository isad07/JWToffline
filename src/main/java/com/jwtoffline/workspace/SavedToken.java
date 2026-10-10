package com.jwtoffline.workspace;

import java.time.Instant;
import java.util.Objects;

/** A token the user saved to the local workspace with a label. */
public final class SavedToken {

    private final String id;
    private final String label;
    private final String token;
    private final Instant savedAt;

    public SavedToken(String id, String label, String token, Instant savedAt) {
        this.id = Objects.requireNonNull(id);
        this.label = Objects.requireNonNull(label);
        this.token = Objects.requireNonNull(token);
        this.savedAt = Objects.requireNonNull(savedAt);
    }

    public String id() { return id; }
    public String label() { return label; }
    public String token() { return token; }
    public Instant savedAt() { return savedAt; }

    public SavedToken withLabel(String newLabel) {
        return new SavedToken(id, newLabel, token, savedAt);
    }

    @Override
    public String toString() {
        return label;
    }
}
