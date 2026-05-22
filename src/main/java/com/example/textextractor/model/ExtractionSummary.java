package com.example.textextractor.model;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ExtractionSummary(
        int totalFilesScanned,
        int totalFilesWithExtractedText,
        int totalExtractedTextOccurrences,
        int totalUniqueTexts,
        List<FileTextCount> topFilesWithMostTexts,
        Set<String> ignoredFolders,
        List<String> errors
) {
    public static ExtractionSummary from(
            List<Path> scannedFiles,
            List<ExtractedText> extractedTexts,
            Set<String> ignoredFolders,
            List<String> errors
    ) {
        Map<String, Long> byFile = extractedTexts.stream()
                .map(ExtractedText::file)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<FileTextCount> topFiles = byFile.entrySet().stream()
                .map(entry -> new FileTextCount(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(FileTextCount::count).reversed().thenComparing(FileTextCount::file))
                .limit(10)
                .toList();

        int uniqueTexts = (int) extractedTexts.stream()
                .map(ExtractedText::text)
                .distinct()
                .count();

        return new ExtractionSummary(
                scannedFiles.size(),
                byFile.size(),
                extractedTexts.size(),
                uniqueTexts,
                topFiles,
                ignoredFolders,
                List.copyOf(errors)
        );
    }

    public record FileTextCount(String file, long count) {
    }
}
