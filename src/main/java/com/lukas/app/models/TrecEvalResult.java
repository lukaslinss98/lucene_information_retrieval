package com.lukas.app.models;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

public record TrecEvalResult(
        Analyzer usedAnalyzer,
        Similarity usedSimilarity,
        Float meanAveragePrecision,
        Float recallAtFive,
        Float recallAtTen,
        Float recallAtTwenty,
        Float precisionAtFive,
        Float precisionAtTen,
        Float precisionAtTwenty
) {

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return """
            Analyzer: %s
            Similarity: %s
            mean average precision (MAP): %.4f
            Recall@5: %.4f
            Recall@10: %.4f
            Recall@20: %.4f
            Precision@5: %.4f
            Precision@10: %.4f
            Precision@20: %.4f
            """.formatted(
                usedAnalyzer.getClass().getSimpleName(),
                usedSimilarity.getClass().getSimpleName(),
                meanAveragePrecision,
                recallAtFive,
                recallAtTen,
                recallAtTwenty,
                precisionAtFive,
                precisionAtTen,
                precisionAtTwenty
        );
    }

}
