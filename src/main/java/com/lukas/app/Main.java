package com.lukas.app;

import com.google.common.collect.Streams;
import com.lukas.app.models.CommandLineArguments;
import com.lukas.app.models.CranfieldDocument;
import com.lukas.app.models.QueryResult;
import com.lukas.app.services.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final Analyzer DEFAULT_ANALYZER = new SimpleAnalyzer();
    private static final Similarity DEFAULT_SIMILARITY = new ClassicSimilarity();
    private static final Path INDEX_PATH = Paths.get("../index");

    public static void main(String[] args) throws URISyntaxException, IOException {
        CommandLineArguments arguments = CommandLineArgumentsService.parseArgs(List.of(args));

        IndexService indexService = new IndexService(
                INDEX_PATH,
                arguments.analyzer().orElse(DEFAULT_ANALYZER),
                arguments.similarity().orElse(DEFAULT_SIMILARITY)
        );
        QueryService queryService = QueryService.create(
                INDEX_PATH,
                arguments.analyzer().orElse(DEFAULT_ANALYZER),
                arguments.similarity().orElse(DEFAULT_SIMILARITY)
        );


        String rawCollection = FileReader.readFiles("/cran.all.1400");
        String rawQueryFile = FileReader.readFiles("/cran.qry");

        List<Document> documents = Arrays.stream(rawCollection.split("(?=\\.I)"))
                .map(CranfieldParser::parseDocument)
                .map(CranfieldDocument::toLuceneDocument)
                .toList();

        indexService.addDocuments(documents);

        String[] rawQueries = rawQueryFile.split("(?=\\.I)");

        String trecEvalResults = Streams.mapWithIndex(
                        Arrays.stream(rawQueries),
                        (rawQuery, i) -> CranfieldParser.parseQuery(rawQuery, i + 1))
                .map(queryService::query)
                .map(QueryResult::asTrecEvalResult)
                .collect(Collectors.joining("\n"));

        TrecEvalRunner.run(trecEvalResults);
    }
}
