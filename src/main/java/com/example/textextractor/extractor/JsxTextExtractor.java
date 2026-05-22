package com.example.textextractor.extractor;

import com.example.textextractor.config.ExtractionConfig;
import com.example.textextractor.model.SourceType;

public final class JsxTextExtractor extends AbstractTagTextExtractor {
    public JsxTextExtractor(ExtractionConfig config) {
        super(config, SourceType.JSX_TEXT);
    }
}
