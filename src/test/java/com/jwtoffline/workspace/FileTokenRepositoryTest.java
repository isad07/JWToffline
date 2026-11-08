package com.jwtoffline.workspace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.jwtoffline.core.JwtException;

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
    void failedWritesPreserveTokensAndAllowRetry() throws Exception {
        Path parent = dir.resolve("storage");
        Path moved = dir.resolve("storage-backup");
        Path workspace = parent.resolve("workspace.json");
        FileTokenRepository repo = new FileTokenRepository(workspace);
        SavedToken saved = repo.save("original", "a.b.c");
        String originalFile = Files.readString(workspace);
        Files.move(parent, moved);
        Files.writeString(parent, "block directory creation");

        assertThrows(JwtException.class, () -> repo.save("new", "d.e.f"));
        assertEquals(List.of(saved), repo.findAll());
        assertThrows(JwtException.class, () -> repo.rename(saved.id(), "changed"));
        assertEquals(List.of(saved), repo.findAll());
        assertThrows(JwtException.class, () -> repo.delete(saved.id()));
        assertEquals(List.of(saved), repo.findAll());
        assertEquals(originalFile, Files.readString(moved.resolve("workspace.json")));

        Files.delete(parent);
        Files.move(moved, parent);
        assertTrue(repo.rename(saved.id(), "retried"));
        assertEquals("retried", new FileTokenRepository(workspace).findAll().get(0).label());
    }

    @Test
    void initialWriteFailureProducesWarningInsteadOfStoppingStartup() throws Exception {
        Path blocked = dir.resolve("not-a-directory");
        Files.writeString(blocked, "block directory creation");
        FileTokenRepository repo = new FileTokenRepository(blocked.resolve("workspace.json"), true, Clock.systemUTC());
        assertTrue(repo.findAll().isEmpty());
        assertTrue(repo.loadWarning().orElseThrow().contains("Could not create the initial workspace"));
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
