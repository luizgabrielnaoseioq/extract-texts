package com.example.textextractor.extractor;

import com.example.textextractor.config.ExtractionConfig;
import com.example.textextractor.model.ExtractedText;
import com.example.textextractor.model.SourceType;
import com.example.textextractor.util.CommentMasker;
import com.example.textextractor.util.SourceLocationResolver;
import com.example.textextractor.util.TextNormalizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AttributeTextExtractor implements TextExtractor {
    private static final Pattern TAG_PATTERN = Pattern.compile("(?s)<\\s*([A-Za-z][A-Za-z0-9_.:-]*)\\b([^<>]*)>");
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(
            "([A-Za-z_:][A-Za-z0-9_:.-]*)\\s*=\\s*(\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"|'([^'\\\\]*(?:\\\\.[^'\\\\]*)*)'|\\{\\s*\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"\\s*}|\\{\\s*'([^'\\\\]*(?:\\\\.[^'\\\\]*)*)'\\s*})"
    );

    private final ExtractionConfig config;

    public AttributeTextExtractor(ExtractionConfig config) {
        this.config = config;
    }

    @Override
    public List<ExtractedText> extract(String relativeFile, String content) {
        String maskedContent = CommentMasker.mask(content);
        Matcher tagMatcher = TAG_PATTERN.matcher(maskedContent);
        List<ExtractedText> results = new ArrayList<>();

        while (tagMatcher.find()) {
            String tagName = tagMatcher.group(1);
            if (tagName.startsWith("/")) {
                continue;
            }

            String attributes = content.substring(tagMatcher.start(2), tagMatcher.end(2));
            Matcher attributeMatcher = ATTRIBUTE_PATTERN.matcher(attributes);
            while (attributeMatcher.find()) {
                String attributeName = attributeMatcher.group(1);
                String normalizedName = normalizeName(attributeName);
                SourceType sourceType = resolveSourceType(tagName, normalizedName);
                if (sourceType == null) {
                    continue;
                }

                String value = firstPresent(
                        attributeMatcher.group(3),
                        attributeMatcher.group(4),
                        attributeMatcher.group(5),
                        attributeMatcher.group(6)
                );
                String text = TextNormalizer.normalize(unescapeJavaScriptString(value));
                if (!TextNormalizer.isLikelyUserVisible(text, config)) {
                    continue;
                }

                int valueStart = tagMatcher.start(2) + valueStart(attributeMatcher);
                SourceLocationResolver.SourceLocation location = SourceLocationResolver.resolve(content, valueStart);
                results.add(new ExtractedText(
                        text,
                        relativeFile,
                        location.line(),
                        location.column(),
                        sourceType,
                        TextNormalizer.normalize(content.substring(tagMatcher.start(), tagMatcher.end()))
                ));
            }
        }

        return results;
    }

    private SourceType resolveSourceType(String tagName, String normalizedName) {
        if (config.ignoredAttributeNames().contains(normalizedName)) {
            return null;
        }

        boolean component = Character.isUpperCase(tagName.charAt(0)) || tagName.contains(".");
        if (component && config.componentPropNames().contains(normalizedName)) {
            return SourceType.COMPONENT_PROP;
        }
        if (config.visibleAttributes().contains(normalizedName)) {
            return SourceType.ATTRIBUTE;
        }
        return null;
    }

    private static String normalizeName(String attributeName) {
        return attributeName.toLowerCase(Locale.ROOT);
    }

    private static String firstPresent(String... values) {
        for (String value : values) {
            if (value != null) {
                return value;
            }
        }
        return "";
    }

    private static int valueStart(Matcher matcher) {
        for (int group = 3; group <= 6; group++) {
            if (matcher.group(group) != null) {
                return matcher.start(group);
            }
        }
        return matcher.start(2);
    }

    private static String unescapeJavaScriptString(String value) {
        return value
                .replace("\\\"", "\"")
                .replace("\\'", "'")
                .replace("\\n", " ")
                .replace("\\r", " ")
                .replace("\\t", " ");
    }
}
