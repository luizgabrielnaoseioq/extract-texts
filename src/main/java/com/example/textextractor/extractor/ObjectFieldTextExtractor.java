package com.example.textextractor.extractor;

import com.example.textextractor.config.ExtractionConfig;
import com.example.textextractor.model.ExtractedText;
import com.example.textextractor.model.SourceType;
import com.example.textextractor.util.CommentMasker;
import com.example.textextractor.util.SourceLocationResolver;
import com.example.textextractor.util.TextNormalizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ObjectFieldTextExtractor implements TextExtractor {
    private static final Pattern OBJECT_FIELD_PATTERN = Pattern.compile(
            "(?m)(?<![\\w$\"'])['\"]?([A-Za-z_$][A-Za-z0-9_$-]*)['\"]?\\s*:\\s*(\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"|'([^'\\\\]*(?:\\\\.[^'\\\\]*)*)')"
    );

    private final ExtractionConfig config;

    public ObjectFieldTextExtractor(ExtractionConfig config) {
        this.config = config;
    }

    @Override
    public List<ExtractedText> extract(String relativeFile, String content) {
        String maskedContent = CommentMasker.mask(content);
        Matcher matcher = OBJECT_FIELD_PATTERN.matcher(maskedContent);
        List<ExtractedText> results = new ArrayList<>();

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            if (!config.objectFieldNames().contains(fieldName)) {
                continue;
            }

            String value = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
            String text = TextNormalizer.normalize(unescapeJavaScriptString(value));
            if (!TextNormalizer.isLikelyUserVisible(text, config)) {
                continue;
            }

            int valueStart = matcher.group(3) != null ? matcher.start(3) : matcher.start(4);
            SourceLocationResolver.SourceLocation location = SourceLocationResolver.resolve(content, valueStart);
            results.add(new ExtractedText(
                    text,
                    relativeFile,
                    location.line(),
                    location.column(),
                    SourceType.OBJECT_FIELD,
                    TextNormalizer.normalize(SourceLocationResolver.currentLine(content, valueStart))
            ));
        }

        return results;
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
