package com.example.textextractor.util;

import com.example.textextractor.config.ExtractionConfig;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class TextNormalizer {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern HAS_LETTER_OR_DIGIT = Pattern.compile(".*[\\p{L}\\p{N}].*");
    private static final Pattern ONLY_SYMBOLS = Pattern.compile("^[\\p{P}\\p{S}\\s]+$");
    private static final Pattern URL_OR_ROUTE = Pattern.compile("(?i)^(https?:|mailto:|tel:|/|\\./|\\.\\./|@/|[a-z]:\\\\).*");
    private static final Pattern FILE_PATH = Pattern.compile(".*(\\\\|/).+\\.[A-Za-z0-9]{1,8}$");
    private static final Pattern TECHNICAL_CONSTANT = Pattern.compile("^[A-Z0-9_\\-.]+$");
    private static final Pattern SLUG_OR_KEY = Pattern.compile("^[a-z0-9]+([-_:.][a-z0-9]+)+$");
    private static final Set<String> ALLOWED_SHORT_TEXTS = Set.of("ok", "no", "yes", "on", "off");
    private static final Set<String> COMMON_CSS_TOKENS = Set.of(
            "absolute", "block", "border", "center", "container", "flex", "fixed", "gap", "grid", "hidden",
            "inline", "items", "justify", "left", "mt", "mx", "my", "p", "px", "py", "relative", "right",
            "rounded", "shadow", "text", "top", "w"
    );

    private TextNormalizer() {
    }

    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        return WHITESPACE.matcher(decodeBasicHtmlEntities(text).trim()).replaceAll(" ");
    }

    public static boolean isLikelyUserVisible(String text, ExtractionConfig config) {
        String normalized = normalize(text);
        if (normalized.isEmpty()) {
            return false;
        }
        if (!HAS_LETTER_OR_DIGIT.matcher(normalized).matches() || ONLY_SYMBOLS.matcher(normalized).matches()) {
            return false;
        }
        if (URL_OR_ROUTE.matcher(normalized).matches() || FILE_PATH.matcher(normalized).matches()) {
            return false;
        }

        String lower = normalized.toLowerCase(Locale.ROOT);
        if (normalized.length() <= 2 && !ALLOWED_SHORT_TEXTS.contains(lower)) {
            return false;
        }
        if (TECHNICAL_CONSTANT.matcher(normalized).matches() && !ALLOWED_SHORT_TEXTS.contains(lower)) {
            return false;
        }
        if (SLUG_OR_KEY.matcher(normalized).matches()) {
            return false;
        }
        return !looksLikeCssClassList(lower);
    }

    private static boolean looksLikeCssClassList(String text) {
        String[] tokens = text.split("\\s+");
        if (tokens.length < 2) {
            return false;
        }

        int cssLikeTokens = 0;
        for (String token : tokens) {
            String base = token;
            int dashIndex = base.indexOf('-');
            if (dashIndex > 0) {
                base = base.substring(0, dashIndex);
            }
            int colonIndex = base.indexOf(':');
            if (colonIndex > 0) {
                base = base.substring(colonIndex + 1);
            }

            if (COMMON_CSS_TOKENS.contains(base) || token.matches("^[a-z]+-[a-z0-9/\\[\\].%#]+$")) {
                cssLikeTokens++;
            }
        }

        return cssLikeTokens == tokens.length;
    }

    private static String decodeBasicHtmlEntities(String text) {
        return text
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");
    }
}
