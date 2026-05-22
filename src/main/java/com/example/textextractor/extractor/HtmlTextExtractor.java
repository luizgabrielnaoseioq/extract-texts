package com.example.textextractor.extractor;

import com.example.textextractor.config.ExtractionConfig;
import com.example.textextractor.model.SourceType;

public final class HtmlTextExtractor extends AbstractTagTextExtractor {
    public HtmlTextExtractor(ExtractionConfig config) {
        super(config, SourceType.HTML_TEXT);
    }
}
