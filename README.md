# Text Extractor

Text Extractor is a Java 17 command-line tool that scans frontend projects and extracts text that is likely visible to end users.

It is designed for React, JSX, TSX and HTML codebases where teams need to audit UI copy, prepare localization work, review duplicated labels, or generate an inventory of visible application text.

## Problem Solved

Frontend codebases often contain user-facing text spread across JSX text nodes, HTML tags, component props, accessibility attributes and UI configuration objects. This tool recursively scans a project and produces structured reports that make those texts easier to review.

The extractor uses pragmatic heuristics instead of a full TSX/JSX parser. That keeps the tool lightweight and easy to maintain while still catching the most common UI text patterns.

## Features

- Recursively scans `.tsx`, `.jsx` and `.html` files.
- Ignores common generated or dependency folders.
- Extracts JSX and HTML text nodes.
- Extracts user-facing attributes such as `placeholder`, `title`, `alt`, `aria-label`, `label` and `value`.
- Extracts common component props such as `title`, `description`, `label`, `text` and `placeholder`.
- Extracts UI-like object fields such as `label`, `title`, `description` and `message`.
- Preserves duplicated text occurrences.
- Reports total unique texts.
- Normalizes whitespace.
- Keeps UTF-8 and accented characters.
- Skips routes, URLs, imports, class names, test IDs, constants and other technical-looking strings where possible.
- Continues scanning when a file cannot be read and reports the error.
- Generates JSON, CSV and Markdown output.

## Requirements

- Java 17 or later
- Maven 3.9 or later

## Project Structure

```text
src/main/java/com/example/textextractor/
  cli/        CLI argument parsing and application orchestration
  config/     Extraction rules and supported folders/extensions
  scanner/    Recursive project scanner
  extractor/  Heuristic extractors for JSX, HTML, attributes, props and object fields
  model/      Output records and source type enum
  writer/     JSON, CSV and Markdown output writers
  util/       Text normalization, comment masking and source location helpers

src/test/java/com/example/textextractor/
  extractor/  Core extraction tests
  scanner/    Scanner tests
  writer/     CSV escaping tests

samples/frontend/
  Small frontend sample for demonstration
```

## Install Dependencies

Maven downloads dependencies automatically:

```bash
mvn dependency:resolve
```

The project uses:

- Jackson for JSON writing
- JUnit 5 for tests

## Build

```bash
mvn clean package
```

The runnable JAR is generated at:

```text
target/text-extractor.jar
```

## Run

```bash
java -jar target/text-extractor.jar --input ./my-frontend-project --output ./texts-output
```

## CLI Parameters

| Parameter | Required | Description |
| --- | --- | --- |
| `--input` | Yes | Frontend project directory to scan. |
| `--output` | Yes | Directory where `texts.json`, `texts.csv` and `summary.md` will be written. |

## Example Usage

Run against the bundled sample:

```bash
mvn clean package
java -jar target/text-extractor.jar --input ./samples/frontend --output ./texts-output
```

Expected output files:

```text
texts-output/
  texts.json
  texts.csv
  summary.md
```

## Output Files

### texts.json

Structured JSON containing every extracted occurrence:

```json
[
  {
    "text": "Save changes",
    "file": "src/App.tsx",
    "line": 12,
    "column": 48,
    "sourceType": "JSX_TEXT",
    "context": "<button title=\"Save current filters\">Save changes</button>"
  }
]
```

### texts.csv

CSV with a header and safe escaping:

```csv
text,file,line,column,sourceType,context
Save changes,src/App.tsx,12,48,JSX_TEXT,"<button title=""Save current filters"">Save changes</button>"
```

### summary.md

Markdown report with aggregate statistics:

```markdown
# Text Extraction Summary

- Total files scanned: 2
- Total files with extracted text: 2
- Total extracted text occurrences: 15
- Total unique texts: 15
```

The summary also includes top files by text count, ignored folders, known limitations and read/scan errors when present.

## Run Tests

```bash
mvn test
```

The test suite covers:

- JSX text extraction
- HTML text extraction
- Attribute extraction
- Component prop extraction
- Object field extraction
- Ignoring imports
- Ignoring Tailwind/class strings
- Ignoring routes and technical constants
- Whitespace normalization
- CSV escaping
- UTF-8 text
- Ignored scan folders

## How Extraction Works

The tool applies several independent extractors:

1. `JsxTextExtractor` finds text between JSX tags in `.tsx` and `.jsx` files.
2. `HtmlTextExtractor` finds text between HTML tags in `.html` files.
3. `AttributeTextExtractor` finds user-facing attributes and component props.
4. `ObjectFieldTextExtractor` finds UI-like object fields.

Before extraction, comments are masked while preserving character offsets. This keeps line and column resolution stable and avoids extracting commented-out UI copy.

Each candidate text is normalized and then filtered by safety heuristics.

## What Is Extracted

Examples:

```tsx
<h1>Dashboard</h1>
<button>Save changes</button>
<input placeholder="Search customers" aria-label="Customer search" />
<PageHeader title="Settings" description="Manage your preferences" />

const menuItems = [
  { label: "Home", description: "Go to the home page" }
];
```

## What Is Ignored

Examples:

```tsx
import Button from "@/components/Button";
const route = "/dashboard";
const className = "flex items-center gap-2";
const status = "PENDING";
const queryKey = "users-list";
<button data-testid="save-button" className="px-4 py-2">Save</button>
```

Ignored folders:

- `node_modules`
- `dist`
- `build`
- `.next`
- `out`
- `coverage`
- `.git`
- `.turbo`
- `.cache`

## Known Limitations

- This is a heuristic extractor, not a complete TSX/JSX parser.
- Dynamic expressions are not fully resolved.
- Template strings are not evaluated.
- Translation function calls such as `t("dashboard.title")` are not expanded.
- Complex nested JSX expressions can require manual review.
- Some technical strings can be extracted when they use UI-like field names.
- Some valid UI labels can be ignored when they look like routes, slugs, IDs or constants.

## Future Improvements

- Add optional parser-based extraction for TSX/JSX.
- Add support for translation function calls.
- Add configurable extraction rules through a YAML or JSON config file.
- Add output grouping by unique text.
- Add severity/confidence scores for each occurrence.
- Add ignore comments for specific files or lines.
- Add support for Vue and Svelte templates.

## Troubleshooting

### `mvn` is not found

Install Maven 3.9 or later and make sure it is available in your `PATH`.

### The JAR does not run

Build the project first:

```bash
mvn clean package
```

Then run:

```bash
java -jar target/text-extractor.jar --input ./samples/frontend --output ./texts-output
```

### No texts were extracted

Check that the input directory exists and contains `.tsx`, `.jsx` or `.html` files outside ignored folders.

### Some text is missing

The extractor intentionally skips strings that look like technical constants, routes, URLs, file paths, slugs or class names. For dynamic UI strings, manual review may still be needed.

### Some extracted text is not user-facing

Heuristics can produce false positives. Review `sourceType`, `file`, `line`, `column` and `context` to decide whether each item belongs in your final inventory.

## Professional Notes

This tool is built for practical text inventory work, not perfect language parsing. Its output should be treated as a strong first pass: fast, consistent and reviewable. For localization or legal copy audits, keep a manual review step because frontend applications can build visible text dynamically at runtime.
