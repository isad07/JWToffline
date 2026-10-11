package com.jwtoffline.workspace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jwtoffline.core.JwtDecoder;
import com.jwtoffline.core.SignatureVerifier;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** US-4.1 to US-4.4 / FR-15 to FR-19 */
class InMemoryTokenRepositoryTest {

    private InMemoryTokenRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryTokenRepository();
    }

    @Test
    void savesAndFindsToken() {
        SavedToken saved = repo.save("  Login token ", "abc.def.ghi ");
        assertEquals("Login token", saved.label());
        assertEquals("abc.def.ghi", saved.token());
        assertEquals(List.of(saved), repo.findAll());
    }

    @Test
    void blankLabelBecomesUntitled() {
        assertEquals("Untitled token", repo.save(" ", "a.b.c").label());
    }

    @Test
    void rejectsEmptyToken() {
        assertThrows(IllegalArgumentException.class, () -> repo.save("x", " "));
    }

    @Test
    void searchMatchesLabelIgnoringCase() {
        repo.save("Staging admin", "a.b.c");
        repo.save("Prod reader", "d.e.f");
        List<SavedToken> hits = repo.search("ADMIN");
        assertEquals(1, hits.size());
        assertEquals("Staging admin", hits.get(0).label());
        assertEquals(2, repo.search("").size());
    }

    @Test
    void renamesAndDeletes() {
        SavedToken t = repo.save("old", "a.b.c");
        assertTrue(repo.rename(t.id(), "new"));
        assertEquals("new", repo.findAll().get(0).label());
        assertFalse(repo.rename(t.id(), " "));
        assertTrue(repo.delete(t.id()));
        assertFalse(repo.delete(t.id()));
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void sampleDataDecodesAndVerifiesWithDemoSecret() {
        InMemoryTokenRepository sample = InMemoryTokenRepository.withSampleData();
        assertEquals(3, sample.findAll().size());
        JwtDecoder decoder = new JwtDecoder();
        SignatureVerifier verifier = new SignatureVerifier();
        for (SavedToken t : sample.findAll()) {
            decoder.decode(t.token());
            assertTrue(verifier.verify(t.token(), SampleTokens.DEMO_SECRET).isValid(), t.label());
        }
    }
}
