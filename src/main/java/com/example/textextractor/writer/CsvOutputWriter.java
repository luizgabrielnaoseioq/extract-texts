package com.example.textextractor.writer;

import com.example.textextractor.model.ExtractedText;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvOutputWriter {
    public void write(Path outputFile, List<ExtractedText> extractedTexts) throws IOException {
        StringBuilder csv = new StringBuilder();
        csv.append("text,file,line,column,sourceType,context").append(System.lineSeparator());
        for (ExtractedText text : extractedTexts) {
            csv.append(escape(text.text())).append(',')
                    .append(escape(text.file())).append(',')
                    .append(text.line()).append(',')
                    .append(text.column()).append(',')
                    .append(text.sourceType()).append(',')
                    .append(escape(text.context()))
                    .append(System.lineSeparator());
        }
        Files.writeString(outputFile, csv.toString(), StandardCharsets.UTF_8);
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean mustQuote = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return mustQuote ? "\"" + escaped + "\"" : escaped;
    }
}
