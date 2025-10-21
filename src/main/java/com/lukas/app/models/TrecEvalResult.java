package com.lukas.app.models;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

public record TrecEvalResult(
        Analyzer usedAnalyzer,
        Similarity usedSimilarity,
        Float meanAveragePrecision,
        Float recall,
        Float precisionAtFive
) {

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "Analyzer: %s,\nSimilar: %s,\nmean average precision: %s,\nrecall: %s,\nprecision at 5: %s\n"
                .formatted(
                        usedAnalyzer.getClass().getSimpleName(),
                        usedSimilarity.getClass().getSimpleName(),
                        meanAveragePrecision,
                        recall,
                        precisionAtFive
                );
    }
}
