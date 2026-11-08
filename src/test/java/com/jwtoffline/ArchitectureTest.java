package com.jwtoffline;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Automated code review checks.
 * NFR-1 (privacy): no source file uses networking classes.
 * NFR-7 (maintainability): core and workspace packages have no JavaFX code, so logic stays testable on its own.
 */
class ArchitectureTest {

    private static final Path MAIN = Path.of("src", "main", "java", "com", "jwtoffline");

    private static List<Path> javaFiles(Path root) throws IOException {
        try (Stream<Path> files = Files.walk(root)) {
            return files.filter(p -> p.toString().endsWith(".java")).toList();
        }
    }

    private static List<String> filesContaining(Path root, String... needles) throws IOException {
        return javaFiles(root).stream().filter(p -> {
            try {
                String src = Files.readString(p);
                for (String n : needles) {
                    if (src.contains(n)) {
                        return true;
                    }
                }
                return false;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }).map(Path::toString).toList();
    }

    @Test
    void sourcesAreFound() throws IOException {
        assertTrue(javaFiles(MAIN).size() > 20, "run the tests from the project folder");
    }

    @Test
    void noSourceFileUsesNetworking() throws IOException {
        List<String> offenders = filesContaining(MAIN,
                "import java.net.", "java.net.http", "HttpURLConnection", "new Socket(", "java.net.URL");
        assertTrue(offenders.isEmpty(), "network code found in " + offenders);
    }

    @Test
    void corePackageHasNoUiCode() throws IOException {
        List<String> offenders = filesContaining(MAIN.resolve("core"), "import javafx.");
        assertTrue(offenders.isEmpty(), "JavaFX used in core: " + offenders);
    }

    @Test
    void workspacePackageHasNoUiCode() throws IOException {
        List<String> offenders = filesContaining(MAIN.resolve("workspace"), "import javafx.");
        assertTrue(offenders.isEmpty(), "JavaFX used in workspace: " + offenders);
    }
}
