package com.jwtoffline.workspace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** US-4.5 / FR-22: the workspace is kept in a local file and survives closing the app. */
class FileTokenRepositoryTest {

    @TempDir
    Path dir;

    private Path file() {
        return dir.resolve("workspace.json");
    }

    @Test
    void savedTokensSurviveReopening() {
        FileTokenRepository first = new FileTokenRepository(file());
        SavedToken saved = first.save("Login token", SampleTokens.ADMIN_SESSION);

        FileTokenRepository reopened = new FileTokenRepository(file());
        assertEquals(List.of(saved), reopened.findAll());
    }

    @Test
    void renameAndDeleteArePersisted() {
        FileTokenRepository repo = new FileTokenRepository(file());
        SavedToken keep = repo.save("keep", "a.b.c");
        SavedToken drop = repo.save("drop", "d.e.f");
        assertTrue(repo.rename(keep.id(), "renamed"));
        assertTrue(repo.delete(drop.id()));

        List<SavedToken> reloaded = new FileTokenRepository(file()).findAll();
        assertEquals(1, reloaded.size());
        assertEquals("renamed", reloaded.get(0).label());
        assertEquals(keep.id(), reloaded.get(0).id());
    }

    @Test
    void keepsSaveTimeAndOrderAfterReload() {
        Clock early = Clock.fixed(Instant.parse("2026-11-01T10:00:00Z"), ZoneOffset.UTC);
        Clock late = Clock.fixed(Instant.parse("2026-11-02T10:00:00Z"), ZoneOffset.UTC);
        new FileTokenRepository(file(), false, early).save("older", "a.b.c");
        new FileTokenRepository(file(), false, late).save("newer", "d.e.f");

        List<SavedToken> all = new FileTokenRepository(file()).findAll();
        assertEquals("newer", all.get(0).label());
        assertEquals(Instant.parse("2026-11-01T10:00:00Z"), all.get(1).savedAt());
    }

    @Test
    void searchWorksAfterReload() {
        FileTokenRepository repo = new FileTokenRepository(file());
        repo.save("Staging admin", "a.b.c");
        repo.save("Prod reader", "d.e.f");
        assertEquals(1, new FileTokenRepository(file()).search("admin").size());
    }

    @Test
    void firstLaunchCreatesFileWithSampleTokens() {
        Path nested = dir.resolve("new-folder").resolve("workspace.json");
        FileTokenRepository repo = new FileTokenRepository(nested, true, Clock.systemUTC());
        assertEquals(SampleTokens.ALL.size(), repo.findAll().size());
        assertTrue(Files.exists(nested));
    }

    @Test
    void missingFileWithoutSeedingStartsEmpty() {
        FileTokenRepository repo = new FileTokenRepository(file());
        assertTrue(repo.findAll().isEmpty());
        assertFalse(Files.exists(file()));
        assertTrue(repo.loadWarning().isEmpty());
    }

    @Test
    void corruptFileStartsEmptyAndKeepsBackup() throws Exception {
        Files.writeString(file(), "{ this is not json");
        FileTokenRepository repo = new FileTokenRepository(file());

        assertTrue(repo.findAll().isEmpty());
        assertTrue(repo.loadWarning().isPresent());
        Path backup = dir.resolve("workspace.json.corrupt");
        assertEquals("{ this is not json", Files.readString(backup));
    }

    @Test
    void entryWithMissingFieldsIsTreatedAsCorrupt() throws Exception {
        Files.writeString(file(), "[{\"id\":\"1\",\"label\":\"x\"}]");
        FileTokenRepository repo = new FileTokenRepository(file());
        assertTrue(repo.findAll().isEmpty());
        assertTrue(repo.loadWarning().isPresent());
    }

    @Test
    void fileStoresOnlyTokensAndLabels() throws Exception {
        FileTokenRepository repo = new FileTokenRepository(file());
        repo.save("Admin", SampleTokens.ADMIN_SESSION);
        String json = Files.readString(file());
        assertTrue(json.contains("\"label\": \"Admin\""));
        assertFalse(json.contains(SampleTokens.DEMO_SECRET), "the signing secret must never be written to disk");
    }
}
