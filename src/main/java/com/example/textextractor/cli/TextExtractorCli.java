package com.example.textextractor.cli;

import com.example.textextractor.config.ExtractionConfig;
import com.example.textextractor.extractor.TextExtractor;
import com.example.textextractor.extractor.VisibleTextExtractionService;
import com.example.textextractor.model.ExtractedText;
import com.example.textextractor.model.ExtractionSummary;
import com.example.textextractor.scanner.FileScanResult;
import com.example.textextractor.scanner.ProjectScanner;
import com.example.textextractor.writer.CsvOutputWriter;
import com.example.textextractor.writer.JsonOutputWriter;
import com.example.textextractor.writer.MarkdownSummaryWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class TextExtractorCli {
    private TextExtractorCli() {
    }

    public static void main(String[] args) {
        int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args) {
        if (args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]))) {
            printUsage();
            return 0;
        }

        CliOptions options;
        try {
            options = CliOptions.parse(args);
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            printUsage();
            return 1;
        }

        ExtractionConfig config = ExtractionConfig.defaultConfig();
        ProjectScanner scanner = new ProjectScanner(config);
        TextExtractor extractor = new VisibleTextExtractionService(config);

        try {
            Files.createDirectories(options.outputDirectory());
            FileScanResult scanResult = scanner.scan(options.inputDirectory());
            List<ExtractedText> extractedTexts = extractTexts(options.inputDirectory(), scanResult, extractor);
            ExtractionSummary summary = ExtractionSummary.from(
                    scanResult.files(),
                    extractedTexts,
                    config.ignoredDirectories(),
                    scanResult.errors()
            );

            new JsonOutputWriter().write(options.outputDirectory().resolve("texts.json"), extractedTexts);
            new CsvOutputWriter().write(options.outputDirectory().resolve("texts.csv"), extractedTexts);
            new MarkdownSummaryWriter().write(options.outputDirectory().resolve("summary.md"), summary);

            System.out.printf("Scanned %d files and extracted %d text occurrences.%n",
                    summary.totalFilesScanned(),
                    summary.totalExtractedTextOccurrences());
            System.out.printf("Output written to: %s%n", options.outputDirectory().toAbsolutePath().normalize());
            return 0;
        } catch (IOException exception) {
            System.err.println("Failed to write output files: " + exception.getMessage());
            return 1;
        }
    }

    private static List<ExtractedText> extractTexts(Path inputDirectory, FileScanResult scanResult, TextExtractor extractor) {
        List<ExtractedText> extractedTexts = new ArrayList<>();
        for (Path file : scanResult.files()) {
            try {
                String content = Files.readString(file, StandardCharsets.UTF_8);
                String relativeFile = inputDirectory.toAbsolutePath().normalize()
                        .relativize(file.toAbsolutePath().normalize())
                        .toString()
                        .replace('\\', '/');
                extractedTexts.addAll(extractor.extract(relativeFile, content));
            } catch (IOException exception) {
                scanResult.errors().add("Could not read file " + file + ": " + exception.getMessage());
                System.err.println("Could not read file " + file + ": " + exception.getMessage());
            }
        }
        return extractedTexts;
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java -jar text-extractor.jar --input ./my-frontend-project --output ./texts-output");
    }
}
