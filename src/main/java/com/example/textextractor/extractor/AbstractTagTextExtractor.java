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

abstract class AbstractTagTextExtractor implements TextExtractor {
    private static final Pattern TEXT_BETWEEN_TAGS = Pattern.compile("(?s)>([^<>]+)<");

    private final ExtractionConfig config;
    private final SourceType sourceType;

    AbstractTagTextExtractor(ExtractionConfig config, SourceType sourceType) {
        this.config = config;
        this.sourceType = sourceType;
    }

    @Override
    public List<ExtractedText> extract(String relativeFile, String content) {
        String maskedContent = CommentMasker.mask(content);
        Matcher matcher = TEXT_BETWEEN_TAGS.matcher(maskedContent);
        List<ExtractedText> results = new ArrayList<>();

        while (matcher.find()) {
            if (isInsideIgnoredTag(maskedContent, matcher.start())) {
                continue;
            }

            String rawText = content.substring(matcher.start(1), matcher.end(1));
            String text = TextNormalizer.normalize(rawText.replaceAll("\\{[^{}]*}", " "));
            if (!TextNormalizer.isLikelyUserVisible(text, config)) {
                continue;
            }

            SourceLocationResolver.SourceLocation location = SourceLocationResolver.resolve(content, matcher.start(1));
            results.add(new ExtractedText(
                    text,
                    relativeFile,
                    location.line(),
                    location.column(),
                    sourceType,
                    TextNormalizer.normalize(SourceLocationResolver.contextAround(content, matcher.start(1), matcher.end(1)))
            ));
        }

        return results;
    }

    private boolean isInsideIgnoredTag(String content, int textStart) {
        int previousTagStart = content.lastIndexOf('<', textStart);
        int previousTagEnd = content.indexOf('>', previousTagStart);
        if (previousTagStart < 0 || previousTagEnd < 0 || previousTagEnd > textStart) {
            return false;
        }

        String tag = content.substring(previousTagStart, Math.min(previousTagEnd + 1, content.length())).toLowerCase();
        return tag.startsWith("<script") || tag.startsWith("<style");
    }
}
