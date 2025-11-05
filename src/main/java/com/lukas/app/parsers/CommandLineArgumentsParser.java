package com.lukas.app.parsers;

import com.lukas.app.analyzers.CustomAnalyzer;
import com.lukas.app.analyzers.PerFieldAnalyzer;
import com.lukas.app.models.CommandLineArguments;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandLineArgumentsParser {

    public static CommandLineArguments parseArgs(List<String> commandLineArgs) {
        List<Analyzer> analyzers = new ArrayList<>();
        List<Similarity> similarities = new ArrayList<>();

        for (int i = 0; i < commandLineArgs.size(); i++) {
            String arg = commandLineArgs.get(i);

            if (arg.equals("-a") && i + 1 < commandLineArgs.size()) {
                analyzers.addAll(createAnalyzers(commandLineArgs.get(i + 1)));
            }

            if (arg.equals("-s") && i + 1 < commandLineArgs.size()) {
                similarities.addAll(createSimilarities(commandLineArgs.get(i + 1)));
            }
        }

        return new CommandLineArguments(
                analyzers,
                similarities
        );
    }

    private static List<Analyzer> createAnalyzers(String analyzers) {
        return Arrays.stream(analyzers.split(","))
                .map(CommandLineArgumentsParser::createAnalyzer)
                .toList();
    }

    private static List<Similarity> createSimilarities(String similarities) {
        return Arrays.stream(similarities.split(","))
                .map(CommandLineArgumentsParser::createSimilarity)
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
            case "perfield" -> PerFieldAnalyzer.create();
            default -> throw new IllegalArgumentException(
                    "Unknown analyzers: '%s'. Supported: standard, english, simple, whitespace, stop, keyword, german, french, spanish"
                            .formatted(analyzerArg)
            );
        };
    }

    private static Similarity createSimilarity(String similarityArg) {
        return switch (similarityArg.toLowerCase()) {
            case "classic" -> new ClassicSimilarity();
            case "bm25" -> new BM25Similarity(1.7f, 0.75f);
            case "boolean" -> new BooleanSimilarity();
            case "lmdirichlet" -> new LMDirichletSimilarity();
            case "lmjelinek" -> new LMJelinekMercerSimilarity(0.7f);
            default -> throw new IllegalArgumentException(
                    "Unknown similarities: '%s'. Supported: classic, bm25, boolean, lmdirichlet, ljm"
                            .formatted(similarityArg)
            );
        };
    }

}
