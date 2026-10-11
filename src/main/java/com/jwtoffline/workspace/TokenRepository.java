package com.jwtoffline.workspace;

import java.util.List;

/** Storage operations for saved tokens. */
public interface TokenRepository {

    SavedToken save(String label, String token);

    List<SavedToken> findAll();

    List<SavedToken> search(String text);

    boolean rename(String id, String newLabel);

    boolean delete(String id);
}
