package com.example.textextractor.scanner;

import java.nio.file.Path;
import java.util.List;

public record FileScanResult(List<Path> files, List<String> errors) {
}
