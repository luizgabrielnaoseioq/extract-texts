package com.example.textextractor.util;

public final class SourceLocationResolver {
    private SourceLocationResolver() {
    }

    public static SourceLocation resolve(String content, int offset) {
        int line = 1;
        int column = 1;
        int safeOffset = Math.max(0, Math.min(offset, content.length()));

        for (int index = 0; index < safeOffset; index++) {
            char character = content.charAt(index);
            if (character == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }

        return new SourceLocation(line, column);
    }

    public static String contextAround(String content, int start, int end) {
        int contextStart = content.lastIndexOf('<', start);
        int contextEnd = content.indexOf('>', end);

        if (contextStart >= 0 && contextEnd >= 0) {
            return content.substring(contextStart, Math.min(contextEnd + 1, content.length()));
        }

        return currentLine(content, start);
    }

    public static String currentLine(String content, int offset) {
        int safeOffset = Math.max(0, Math.min(offset, content.length()));
        int lineStart = content.lastIndexOf('\n', safeOffset);
        int lineEnd = content.indexOf('\n', safeOffset);

        if (lineStart < 0) {
            lineStart = 0;
        } else {
            lineStart++;
        }
        if (lineEnd < 0) {
            lineEnd = content.length();
        }

        return content.substring(lineStart, lineEnd);
    }

    public record SourceLocation(int line, int column) {
    }
}
