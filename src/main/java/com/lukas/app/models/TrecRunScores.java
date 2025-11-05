package com.lukas.app.models;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

public record TrecRunScores(
        Analyzer analyzer,
        Similarity similarity,
        String scores
) {
}
