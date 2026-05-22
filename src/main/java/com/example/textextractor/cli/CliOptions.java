package com.example.textextractor.cli;

import java.nio.file.Files;
import java.nio.file.Path;

record CliOptions(Path inputDirectory, Path outputDirectory) {
    static CliOptions parse(String[] args) {
        Path input = null;
        Path output = null;

        for (int index = 0; index < args.length; index++) {
            String arg = args[index];
            if ("--input".equals(arg)) {
                input = readPathValue(args, ++index, "--input");
            } else if ("--output".equals(arg)) {
                output = readPathValue(args, ++index, "--output");
            } else if ("--help".equals(arg) || "-h".equals(arg)) {
                throw new IllegalArgumentException("Text Extractor CLI");
            } else {
                throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }

        if (input == null) {
            throw new IllegalArgumentException("Missing required argument: --input");
        }
        if (output == null) {
            throw new IllegalArgumentException("Missing required argument: --output");
        }
        if (!Files.isDirectory(input)) {
            throw new IllegalArgumentException("Input directory does not exist: " + input);
        }

        return new CliOptions(input.toAbsolutePath().normalize(), output.toAbsolutePath().normalize());
    }

    private static Path readPathValue(String[] args, int index, String optionName) {
        if (index >= args.length || args[index].startsWith("--")) {
            throw new IllegalArgumentException("Missing value for " + optionName);
        }
        return Path.of(args[index]);
    }
}
