package com.example.textextractor.scanner;

import com.example.textextractor.config.ExtractionConfig;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ProjectScanner {
    private final ExtractionConfig config;

    public ProjectScanner(ExtractionConfig config) {
        this.config = config;
    }

    public FileScanResult scan(Path inputDirectory) throws IOException {
        List<Path> files = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        Files.walkFileTree(inputDirectory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (!dir.equals(inputDirectory) && config.ignoredDirectories().contains(dir.getFileName().toString())) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.isRegularFile() && config.supportedExtensions().contains(extensionOf(file))) {
                    files.add(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exception) {
                errors.add("Could not visit " + file + ": " + exception.getMessage());
                System.err.println("Could not visit " + file + ": " + exception.getMessage());
                return FileVisitResult.CONTINUE;
            }
        });

        return new FileScanResult(files, errors);
    }

    private static String extensionOf(Path file) {
        String fileName = file.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return fileName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }
}
