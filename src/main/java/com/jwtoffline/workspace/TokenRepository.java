package com.jwtoffline.workspace;

import java.util.List;
import java.util.Optional;

/**
 * Storage for saved tokens. The prototype uses an in memory implementation with mock data.
 * A file backed implementation (JSON in the user's home folder) replaces it in a later iteration
 * without changing any UI code.
 */
public interface TokenRepository {

    SavedToken save(String label, String token);

    List<SavedToken> findAll();

    Optional<SavedToken> findById(String id);

    List<SavedToken> search(String text);

    boolean rename(String id, String newLabel);

    boolean delete(String id);
}
