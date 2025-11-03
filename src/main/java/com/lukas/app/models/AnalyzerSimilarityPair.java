package com.lukas.app.models;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

public record AnalyzerSimilarityPair(
        Analyzer analyzer,
        Similarity similarity
) {
    public static AnalyzerSimilarityPair defaultPair(){
        return new AnalyzerSimilarityPair(
            new SimpleAnalyzer(),
            new ClassicSimilarity()
        );
    }
}
