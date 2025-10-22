package com.lukas.app.models;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public record CommandLineArguments(
       List<Analyzer> analyzers,
       List<Similarity> similarities,
       Optional<Path> qrelsPath
) {
}
