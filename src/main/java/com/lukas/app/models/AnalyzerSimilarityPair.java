package com.lukas.app.models;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

public record AnalyzerSimilarityPair(
        Analyzer analyzer,
        Similarity similarity
) {
}
