package com.lukas.app;

import com.google.common.collect.Streams;
import com.lukas.app.models.*;
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
    private static final Analyzer DEFAULT_ANALYZER = new SimpleAnalyzer();
    private static final Similarity DEFAULT_SIMILARITY = new ClassicSimilarity();

    public static void main(String[] args) throws URISyntaxException, IOException {
        CommandLineArguments arguments = CommandLineArgumentsService.parseArgs(List.of(args));

        List<Analyzer> analyzers = arguments.analyzers();
        List<Similarity> similarities = arguments.similarities();

        List<AnalyzerSimilarityPair> pairs = CombinationsService.createCombinations(
                analyzers,
                similarities,
                () -> new AnalyzerSimilarityPair(DEFAULT_ANALYZER, DEFAULT_SIMILARITY)
        );

        List<TrecEvalResult> trecEvalResults = pairs.stream()
                .map(Main::scoreAnalyzerSimilarityCombination)
                .peek(System.out::println)
                .toList();

        CsvWriter.writeResultsToCsv(trecEvalResults, Path.of("../lucene_evaluation.csv"));
    }

    private static TrecEvalResult scoreAnalyzerSimilarityCombination(AnalyzerSimilarityPair analyzerSimilarityPair) {
        Analyzer analyzer = analyzerSimilarityPair.analyzer();
        Similarity similarity = analyzerSimilarityPair.similarity();

        try (Directory inMemoryDirectory = new ByteBuffersDirectory()) {
            IndexService indexService = new IndexService(
                    analyzer,
                    similarity,
                    inMemoryDirectory
            );

            String rawCollection = FileReader.readFiles("/cran.all.1400");
            String rawQueryFile = FileReader.readFiles("/cran.qry");

            List<Document> documents = Arrays.stream(rawCollection.split("(?=\\.I)"))
                    .map(CranfieldParser::parseDocument)
                    .map(CranfieldDocument::toLuceneDocument)
                    .toList();

            indexService.addDocuments(documents);

            QueryService queryService = QueryService.create(
                    analyzer,
                    similarity,
                    inMemoryDirectory
            );

            String[] rawQueries = rawQueryFile.split("(?=\\.I)");

            String trecEvalResults = Streams.mapWithIndex(
                            Arrays.stream(rawQueries),
                            (rawQuery, i) -> CranfieldParser.parseQuery(rawQuery, i + 1)
                    )
                    .map(queryService::query)
                    .map(QueryResult::asTrecEvalResult)
                    .collect(Collectors.joining("\n"));

            return TrecEvalRunner.run(
                    trecEvalResults,
                    analyzer,
                    similarity
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}