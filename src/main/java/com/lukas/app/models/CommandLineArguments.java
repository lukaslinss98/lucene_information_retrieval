package com.lukas.app.models;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

import java.util.List;

public record CommandLineArguments(
       List<Analyzer> analyzers,
       List<Similarity> similarities
) {
}
