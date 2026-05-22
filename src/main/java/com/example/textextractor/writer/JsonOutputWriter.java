package com.example.textextractor.writer;

import com.example.textextractor.model.ExtractedText;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class JsonOutputWriter {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public void write(Path outputFile, List<ExtractedText> extractedTexts) throws IOException {
        objectMapper.writeValue(outputFile.toFile(), extractedTexts);
    }
}
