package com.example.textextractor.util;

public final class CommentMasker {
    private CommentMasker() {
    }

    public static String mask(String content) {
        StringBuilder output = new StringBuilder(content);
        boolean singleQuoted = false;
        boolean doubleQuoted = false;
        boolean templateQuoted = false;

        for (int index = 0; index < output.length(); index++) {
            char current = output.charAt(index);
            char next = index + 1 < output.length() ? output.charAt(index + 1) : '\0';
            char previous = index > 0 ? output.charAt(index - 1) : '\0';
            boolean escaped = previous == '\\';

            if (!escaped && !doubleQuoted && !templateQuoted && current == '\'') {
                singleQuoted = !singleQuoted;
                continue;
            }
            if (!escaped && !singleQuoted && !templateQuoted && current == '"') {
                doubleQuoted = !doubleQuoted;
                continue;
            }
            if (!escaped && !singleQuoted && !doubleQuoted && current == '`') {
                templateQuoted = !templateQuoted;
                continue;
            }

            if (singleQuoted || doubleQuoted || templateQuoted) {
                continue;
            }

            if (current == '<' && next == '!' && startsWith(output, index, "<!--")) {
                index = maskUntil(output, index, "-->") - 1;
            } else if (current == '/' && next == '*') {
                index = maskUntil(output, index, "*/") - 1;
            } else if (current == '/' && next == '/') {
                index = maskUntilLineEnd(output, index) - 1;
            }
        }

        return output.toString();
    }

    private static boolean startsWith(StringBuilder content, int index, String expected) {
        if (index + expected.length() > content.length()) {
            return false;
        }
        for (int offset = 0; offset < expected.length(); offset++) {
            if (content.charAt(index + offset) != expected.charAt(offset)) {
                return false;
            }
        }
        return true;
    }

    private static int maskUntil(StringBuilder content, int start, String token) {
        int index = start;
        while (index < content.length()) {
            if (startsWith(content, index, token)) {
                int end = Math.min(content.length(), index + token.length());
                maskRange(content, start, end);
                return end;
            }
            index++;
        }
        maskRange(content, start, content.length());
        return content.length();
    }

    private static int maskUntilLineEnd(StringBuilder content, int start) {
        int index = start;
        while (index < content.length() && content.charAt(index) != '\n') {
            index++;
        }
        maskRange(content, start, index);
        return index;
    }

    private static void maskRange(StringBuilder content, int start, int end) {
        for (int index = start; index < end; index++) {
            if (content.charAt(index) != '\n' && content.charAt(index) != '\r') {
                content.setCharAt(index, ' ');
            }
        }
    }
}
