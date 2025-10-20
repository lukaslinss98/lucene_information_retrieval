package com.lukas.app.services;

import com.lukas.app.models.CommandLineArguments;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.*;

import java.util.List;
import java.util.Optional;

public class CommandLineArgumentsService {

    public static CommandLineArguments parseArgs(List<String> commandLineArgs) {
        Analyzer analyzer = null;
        Similarity similarity = null;

        for (int i = 0; i < commandLineArgs.size(); i++) {
            String arg = commandLineArgs.get(i);

            if (arg.equals("--analyzer") && i + 1 < commandLineArgs.size()) {
                analyzer = createAnalyzer(commandLineArgs.get(i + 1));
            }

            if (arg.equals("--similarity") && i + 1 < commandLineArgs.size()) {
                similarity = createSimilarity(commandLineArgs.get(i + 1));
            }
        }

        return new CommandLineArguments(
                Optional.ofNullable(analyzer),
                Optional.ofNullable(similarity)
        );
    }

    private static Analyzer createAnalyzer(String analyzerArg) {
        return switch (analyzerArg.toLowerCase()) {
            case "standard" -> new StandardAnalyzer();
            case "english" -> new EnglishAnalyzer();
            case "simple" -> new SimpleAnalyzer();
            case "whitespace" -> new WhitespaceAnalyzer();
            case "stop" -> new StopAnalyzer(EnglishAnalyzer.getDefaultStopSet());
            case "keyword" -> new KeywordAnalyzer();
            case "german" -> new GermanAnalyzer();
            case "french" -> new FrenchAnalyzer();
            case "spanish" -> new SpanishAnalyzer();
            default -> throw new IllegalArgumentException(
                    "Unknown analyzer: '%s'. Supported: standard, english, simple, whitespace, stop, keyword, german, french, spanish"
                            .formatted(analyzerArg)
            );
        };
    }

    private static Similarity createSimilarity(String similarityArg) {
        return switch (similarityArg.toLowerCase()) {
            case "classic" -> new ClassicSimilarity();
            case "bm25" -> new BM25Similarity();
            case "boolean" -> new BooleanSimilarity();
            case "lmdirichlet" -> new LMDirichletSimilarity();
            case "ljm" -> new LMJelinekMercerSimilarity(0.7f);
            default -> throw new IllegalArgumentException(
                    "Unknown similarity: '%s'. Supported: classic, bm25, boolean, lmdirichlet, ljm"
                            .formatted(similarityArg)
            );
        };
    }
}
