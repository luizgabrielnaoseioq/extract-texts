package com.example.textextractor.extractor;

import com.example.textextractor.config.ExtractionConfig;
import com.example.textextractor.model.ExtractedText;

import java.util.ArrayList;
import java.util.List;

public final class VisibleTextExtractionService implements TextExtractor {
    private final JsxTextExtractor jsxTextExtractor;
    private final HtmlTextExtractor htmlTextExtractor;
    private final AttributeTextExtractor attributeTextExtractor;
    private final ObjectFieldTextExtractor objectFieldTextExtractor;

    public VisibleTextExtractionService(ExtractionConfig config) {
        this.jsxTextExtractor = new JsxTextExtractor(config);
        this.htmlTextExtractor = new HtmlTextExtractor(config);
        this.attributeTextExtractor = new AttributeTextExtractor(config);
        this.objectFieldTextExtractor = new ObjectFieldTextExtractor(config);
    }

    @Override
    public List<ExtractedText> extract(String relativeFile, String content) {
        List<ExtractedText> results = new ArrayList<>();
        boolean htmlFile = relativeFile.toLowerCase().endsWith(".html");

        if (htmlFile) {
            results.addAll(htmlTextExtractor.extract(relativeFile, content));
        } else {
            results.addAll(jsxTextExtractor.extract(relativeFile, content));
            results.addAll(objectFieldTextExtractor.extract(relativeFile, content));
        }

        results.addAll(attributeTextExtractor.extract(relativeFile, content));
        return results;
    }
}
