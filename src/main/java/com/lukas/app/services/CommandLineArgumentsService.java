package com.lukas.app.services;

import com.lukas.app.models.CommandLineArguments;
import com.lukas.app.models.CustomAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandLineArgumentsService {

    public static CommandLineArguments parseArgs(List<String> commandLineArgs) {
        List<Analyzer> analyzers = new ArrayList<>();
        List<Similarity> similarities = new ArrayList<>();

        for (int i = 0; i < commandLineArgs.size(); i++) {
            String arg = commandLineArgs.get(i);

            if (arg.equals("--analyzers") && i + 1 < commandLineArgs.size()) {
                analyzers.addAll(createAnalyzers(commandLineArgs.get(i + 1)));
            }

            if (arg.equals("--similarities") && i + 1 < commandLineArgs.size()) {
                similarities.addAll(createSimilarities(commandLineArgs.get(i + 1)));
            }
        }

        return new CommandLineArguments(
                analyzers,
                similarities
        );
    }

    private static List<Similarity> createSimilarities(String similarities) {
        return Arrays.stream(similarities.split(","))
                .map(CommandLineArgumentsService::createSimilarity)
                .toList();
    }

    private static Analyzer createAnalyzer(String analyzerArg) {
        return switch (analyzerArg.toLowerCase()) {
            case "standard" -> new StandardAnalyzer();
            case "english" -> new EnglishAnalyzer();
            case "simple" -> new SimpleAnalyzer();
            case "whitespace" -> new WhitespaceAnalyzer();
            case "stop" -> new StopAnalyzer(EnglishAnalyzer.getDefaultStopSet());
            case "keyword" -> new KeywordAnalyzer();
            case "custom" -> new CustomAnalyzer();
            case "perfield" -> createPerFieldAnalyzer();
            default -> throw new IllegalArgumentException(
                    "Unknown analyzers: '%s'. Supported: standard, english, simple, whitespace, stop, keyword, german, french, spanish"
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
                    "Unknown similarities: '%s'. Supported: classic, bm25, boolean, lmdirichlet, ljm"
                            .formatted(similarityArg)
            );
        };
    }

    private static Analyzer createPerFieldAnalyzer() {
        Map<String, Analyzer> perField = Map.of(
                "id", new KeywordAnalyzer(),
                "text", new CustomAnalyzer(),
                "title", new EnglishAnalyzer(),
                "author", new KeywordAnalyzer()
        );
        return new PerFieldAnalyzerWrapper(new EnglishAnalyzer(), perField);
    }

    private static List<Analyzer> createAnalyzers(String analyzers) {
        return Arrays.stream(analyzers.split(","))
                .map(CommandLineArgumentsService::createAnalyzer)
                .toList();
    }
}
