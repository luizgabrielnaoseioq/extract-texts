package com.example.textextractor.writer;

import com.example.textextractor.model.ExtractionSummary;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MarkdownSummaryWriter {
    public void write(Path outputFile, ExtractionSummary summary) throws IOException {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# Text Extraction Summary").append(System.lineSeparator()).append(System.lineSeparator());
        markdown.append("- Total files scanned: ").append(summary.totalFilesScanned()).append(System.lineSeparator());
        markdown.append("- Total files with extracted text: ").append(summary.totalFilesWithExtractedText()).append(System.lineSeparator());
        markdown.append("- Total extracted text occurrences: ").append(summary.totalExtractedTextOccurrences()).append(System.lineSeparator());
        markdown.append("- Total unique texts: ").append(summary.totalUniqueTexts()).append(System.lineSeparator());
        markdown.append(System.lineSeparator());

        markdown.append("## Top Files With Most Texts").append(System.lineSeparator()).append(System.lineSeparator());
        if (summary.topFilesWithMostTexts().isEmpty()) {
            markdown.append("No texts were extracted.").append(System.lineSeparator());
        } else {
            markdown.append("| File | Count |").append(System.lineSeparator());
            markdown.append("| --- | ---: |").append(System.lineSeparator());
            for (ExtractionSummary.FileTextCount fileCount : summary.topFilesWithMostTexts()) {
                markdown.append("| `").append(fileCount.file()).append("` | ").append(fileCount.count()).append(" |").append(System.lineSeparator());
            }
        }
        markdown.append(System.lineSeparator());

        markdown.append("## Ignored Folders").append(System.lineSeparator()).append(System.lineSeparator());
        summary.ignoredFolders().stream().sorted()
                .forEach(folder -> markdown.append("- `").append(folder).append("`").append(System.lineSeparator()));
        markdown.append(System.lineSeparator());

        markdown.append("## Limitations").append(System.lineSeparator()).append(System.lineSeparator());
        markdown.append("- The extractor uses heuristics instead of a full TSX/JSX parser.").append(System.lineSeparator());
        markdown.append("- Dynamic expressions, computed translations and template strings may require manual review.").append(System.lineSeparator());
        markdown.append("- Some technical strings can be included when they use UI-like field names.").append(System.lineSeparator());
        markdown.append("- Some valid UI labels can be ignored when they look like routes, IDs, slugs or constants.").append(System.lineSeparator());
        markdown.append(System.lineSeparator());

        if (!summary.errors().isEmpty()) {
            markdown.append("## Read Or Scan Errors").append(System.lineSeparator()).append(System.lineSeparator());
            summary.errors().forEach(error -> markdown.append("- ").append(error).append(System.lineSeparator()));
            markdown.append(System.lineSeparator());
        }

        Files.writeString(outputFile, markdown.toString(), StandardCharsets.UTF_8);
    }
}
