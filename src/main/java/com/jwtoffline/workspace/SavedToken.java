package com.jwtoffline.workspace;

import java.time.Instant;
import java.util.Objects;

/** A token the user saved to the workspace with a label. */
public record SavedToken(String id, String label, String token, Instant savedAt) {

    public SavedToken {
        Objects.requireNonNull(id);
        Objects.requireNonNull(label);
        Objects.requireNonNull(token);
        Objects.requireNonNull(savedAt);
    }

    public SavedToken withLabel(String newLabel) {
        return new SavedToken(id, newLabel, token, savedAt);
    }

    @Override
    public String toString() {
        return label;
    }
}
