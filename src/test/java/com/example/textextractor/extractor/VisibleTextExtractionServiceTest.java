package com.example.textextractor.extractor;

import com.example.textextractor.config.ExtractionConfig;
import com.example.textextractor.model.ExtractedText;
import com.example.textextractor.model.SourceType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisibleTextExtractionServiceTest {
    private final VisibleTextExtractionService extractor = new VisibleTextExtractionService(ExtractionConfig.defaultConfig());

    @Test
    void extractsJsxText() {
        List<ExtractedText> results = extractor.extract("src/App.tsx", """
                export function App() {
                  return <h1>Hello world</h1>;
                }
                """);

        assertContains(results, "Hello world", SourceType.JSX_TEXT);
    }

    @Test
    void extractsHtmlText() {
        List<ExtractedText> results = extractor.extract("public/index.html", """
                <main>
                  <p>Welcome to the system</p>
                </main>
                """);

        assertContains(results, "Welcome to the system", SourceType.HTML_TEXT);
    }

    @Test
    void extractsUserFacingAttributes() {
        List<ExtractedText> results = extractor.extract("src/Search.tsx", """
                export function Search() {
                  return <input placeholder="Search customers" aria-label="Customer search" title="Search field" />;
                }
                """);

        assertContains(results, "Search customers", SourceType.ATTRIBUTE);
        assertContains(results, "Customer search", SourceType.ATTRIBUTE);
        assertContains(results, "Search field", SourceType.ATTRIBUTE);
    }

    @Test
    void extractsComponentProps() {
        List<ExtractedText> results = extractor.extract("src/Header.tsx", """
                export function Header() {
                  return <PageHeader title="Settings" description="Manage your preferences" />;
                }
                """);

        assertContains(results, "Settings", SourceType.COMPONENT_PROP);
        assertContains(results, "Manage your preferences", SourceType.COMPONENT_PROP);
    }

    @Test
    void extractsObjectFields() {
        List<ExtractedText> results = extractor.extract("src/menu.tsx", """
                const menuItems = [
                  { label: "Home", title: "Reports", description: "View all reports" }
                ];
                """);

        assertContains(results, "Home", SourceType.OBJECT_FIELD);
        assertContains(results, "Reports", SourceType.OBJECT_FIELD);
        assertContains(results, "View all reports", SourceType.OBJECT_FIELD);
    }

    @Test
    void ignoresImportPathsAndTechnicalStrings() {
        List<ExtractedText> results = extractor.extract("src/App.tsx", """
                import Button from "@/components/Button";
                const route = "/dashboard";
                const status = "PENDING";
                const queryKey = "users-list";
                const styles = "flex items-center gap-2";
                export function App() {
                  return <div className="flex items-center gap-2" data-testid="save-button">Save</div>;
                }
                """);

        assertContains(results, "Save", SourceType.JSX_TEXT);
        assertNotContains(results, "@/components/Button");
        assertNotContains(results, "/dashboard");
        assertNotContains(results, "PENDING");
        assertNotContains(results, "users-list");
        assertNotContains(results, "flex items-center gap-2");
        assertNotContains(results, "save-button");
    }

    @Test
    void normalizesWhitespaceAndKeepsUtf8Text() {
        List<ExtractedText> results = extractor.extract("src/App.tsx", """
                export function App() {
                  return <p>Olá,    cliente
                    especial</p>;
                }
                """);

        assertContains(results, "Olá, cliente especial", SourceType.JSX_TEXT);
    }

    private static void assertContains(List<ExtractedText> results, String text, SourceType sourceType) {
        assertTrue(
                results.stream().anyMatch(result -> result.text().equals(text) && result.sourceType() == sourceType),
                "Expected to find text '" + text + "' with source type " + sourceType + " in " + results
        );
    }

    private static void assertNotContains(List<ExtractedText> results, String text) {
        assertFalse(
                results.stream().anyMatch(result -> result.text().equals(text)),
                "Expected not to find text '" + text + "' in " + results
        );
    }

    @Test
    void keepsDuplicatedOccurrences() {
        List<ExtractedText> results = extractor.extract("src/App.tsx", """
                export function App() {
                  return <><button>Save</button><Button label="Save" /></>;
                }
                """);

        assertEquals(2, results.stream().filter(result -> result.text().equals("Save")).count());
    }
}
