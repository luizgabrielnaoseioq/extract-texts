package com.example.textextractor.config;

import java.util.Set;

public record ExtractionConfig(
        Set<String> supportedExtensions,
        Set<String> ignoredDirectories,
        Set<String> visibleAttributes,
        Set<String> componentPropNames,
        Set<String> objectFieldNames,
        Set<String> ignoredAttributeNames
) {
    public static ExtractionConfig defaultConfig() {
        Set<String> visibleAttributes = Set.of(
                "alt",
                "aria-label",
                "aria-description",
                "caption",
                "description",
                "helpertext",
                "label",
                "message",
                "placeholder",
                "summary",
                "text",
                "title",
                "tooltip",
                "value"
        );

        return new ExtractionConfig(
                Set.of(".tsx", ".jsx", ".html"),
                Set.of("node_modules", "dist", "build", ".next", "out", "coverage", ".git", ".turbo", ".cache"),
                visibleAttributes,
                Set.of(
                        "actionlabel",
                        "alt",
                        "aria-label",
                        "buttontext",
                        "cancellabel",
                        "canceltext",
                        "caption",
                        "confirmlabel",
                        "confirmtext",
                        "description",
                        "emptytext",
                        "helpertext",
                        "label",
                        "message",
                        "placeholder",
                        "subtitle",
                        "summary",
                        "text",
                        "title",
                        "tooltip",
                        "value"
                ),
                Set.of(
                        "action",
                        "buttonText",
                        "caption",
                        "description",
                        "emptyText",
                        "error",
                        "helperText",
                        "label",
                        "message",
                        "name",
                        "placeholder",
                        "subtitle",
                        "success",
                        "summary",
                        "text",
                        "title",
                        "tooltip",
                        "warning"
                ),
                Set.of(
                        "class",
                        "classname",
                        "data-testid",
                        "data-test-id",
                        "for",
                        "href",
                        "id",
                        "key",
                        "rel",
                        "role",
                        "src",
                        "target",
                        "type"
                )
        );
    }
}
