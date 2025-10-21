package com.lukas.app.services;

import com.lukas.app.models.AnalyzerSimilarityPair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CombinationsService {
    public static List<AnalyzerSimilarityPair> createCombinations(
            List<Analyzer> analyzers,
            List<Similarity> similarities,
            Supplier<AnalyzerSimilarityPair> defaultProvider
    ) {
        if (analyzers.isEmpty() && similarities.isEmpty()) {
            return List.of(defaultProvider.get());
        }
        if (similarities.isEmpty()) {
            return buildPairs(analyzers, List.of(defaultProvider.get().similarity()));
        }
        if (analyzers.isEmpty()) {
            return buildPairs(List.of(defaultProvider.get().analyzer()), similarities);
        }

        return buildPairs(analyzers, similarities);
    }

    private static List<AnalyzerSimilarityPair> buildPairs(List<Analyzer> analyzers, List<Similarity> similarities) {
        return analyzers
                .stream()
                .flatMap(analyzer -> similarities
                        .stream()
                        .map(similarity -> new AnalyzerSimilarityPair(
                                analyzer,
                                similarity
                        ))
                ).toList();
    }
}
