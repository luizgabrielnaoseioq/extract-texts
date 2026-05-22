package com.example.textextractor.scanner;

import com.example.textextractor.config.ExtractionConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectScannerTest {
    @TempDir
    Path tempDir;

    @Test
    void scansSupportedFilesAndIgnoresBuildFolders() throws IOException {
        Files.createDirectories(tempDir.resolve("src"));
        Files.createDirectories(tempDir.resolve("node_modules/pkg"));
        Files.writeString(tempDir.resolve("src/App.tsx"), "<h1>Hello</h1>");
        Files.writeString(tempDir.resolve("src/legacy.js"), "console.log('ignored')");
        Files.writeString(tempDir.resolve("node_modules/pkg/index.tsx"), "<h1>Ignored</h1>");

        FileScanResult result = new ProjectScanner(ExtractionConfig.defaultConfig()).scan(tempDir);

        assertEquals(1, result.files().size());
        assertEquals("App.tsx", result.files().get(0).getFileName().toString());
    }
}
