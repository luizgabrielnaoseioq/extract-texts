package com.example.textextractor.util;

import java.nio.file.Path;
import java.util.Locale;

public final class FileUtils {
    private FileUtils() {
    }

    public static String extensionOf(Path file) {
        String fileName = file.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return fileName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }
}
