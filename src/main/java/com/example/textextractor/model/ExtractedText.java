package com.example.textextractor.model;

public record ExtractedText(
        String text,
        String file,
        int line,
        int column,
        SourceType sourceType,
        String context
) {
}
