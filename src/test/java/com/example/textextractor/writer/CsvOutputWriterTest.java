package com.example.textextractor.writer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvOutputWriterTest {
    @Test
    void escapesCommasQuotesAndLineBreaks() {
        assertEquals("\"Save, now\"", CsvOutputWriter.escape("Save, now"));
        assertEquals("\"Click \"\"Save\"\"\"", CsvOutputWriter.escape("Click \"Save\""));
        assertEquals("\"Line one\nLine two\"", CsvOutputWriter.escape("Line one\nLine two"));
        assertEquals("Plain text", CsvOutputWriter.escape("Plain text"));
    }
}
