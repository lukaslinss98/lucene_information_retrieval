package com.lukas.app.models;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

import java.util.Optional;

public record CommandLineArguments(
       Optional<Analyzer> analyzer,
       Optional<Similarity> similarity
) {
}
