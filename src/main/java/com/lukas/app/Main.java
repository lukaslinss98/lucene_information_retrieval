package com.lukas.app;

import com.google.common.collect.Streams;
import com.lukas.app.io.CsvWriter;
import com.lukas.app.io.FileReader;
import com.lukas.app.models.*;
import com.lukas.app.parsers.CommandLineArgumentsParser;
import com.lukas.app.parsers.CranfieldParser;
import com.lukas.app.services.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        CommandLineArguments arguments = CommandLineArgumentsParser.parseArgs(List.of(args));

        List<AnalyzerSimilarityPair> pairs = CombinationsService.createCombinations(
                arguments.analyzers(),
                arguments.similarities(),
                AnalyzerSimilarityPair::defaultPair
        );

        ScoringService scoringService = new ScoringService();

        List<TrecEvalResult> trecEvalResults = pairs.stream()
                .map(scoringService::scoreAnalyzerSimilarityPair)
                .map(TrecEvalRunner::run)
                .peek(System.out::println)
                .toList();

        CsvWriter.writeResultsToCsv(trecEvalResults, Path.of("cranfield_evaluation.csv"));
    }
}