package com.lukas.app.services;

import com.google.common.collect.Streams;
import com.lukas.app.io.FileReader;
import com.lukas.app.models.AnalyzerSimilarityPair;
import com.lukas.app.models.AnalyzerSimilarityScore;
import com.lukas.app.models.CranfieldDocument;
import com.lukas.app.models.QueryResult;
import com.lukas.app.parsers.CranfieldParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScoringService {

    public AnalyzerSimilarityScore scoreAnalyzerSimilarityPair(AnalyzerSimilarityPair pair) {
        Analyzer analyzer = pair.analyzer();
        Similarity similarity = pair.similarity();

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

            return Streams.mapWithIndex(
                            Arrays.stream(rawQueries),
                            (rawQuery, i) -> CranfieldParser.parseQuery(rawQuery, i + 1)
                    )
                    .map(queryService::query)
                    .map(QueryResult::asTrecEvalResult)
                    .collect(Collectors.collectingAndThen(
                                    Collectors.joining("\n"),
                                    score -> new AnalyzerSimilarityScore(analyzer, similarity, score)
                            )
                    );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
