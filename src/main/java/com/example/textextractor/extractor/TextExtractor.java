package com.example.textextractor.extractor;

import com.example.textextractor.model.ExtractedText;

import java.util.List;

public interface TextExtractor {
    List<ExtractedText> extract(String relativeFile, String content);
}
