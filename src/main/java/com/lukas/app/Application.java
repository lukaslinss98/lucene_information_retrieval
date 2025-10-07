package com.lukas.app;

import com.google.common.collect.Streams;
import com.lukas.app.models.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lukas.app.CranfieldParser.parseQuery;
import static java.util.stream.Collectors.collectingAndThen;

public class Application {
    private static final Analyzer ANALYZER = new StandardAnalyzer();
    private static final String INDEX_DIRECTORY_PATH = "../index";

    public static void main(String[] args) throws URISyntaxException, IOException {
        Directory indexDir = FSDirectory.open(Paths.get(INDEX_DIRECTORY_PATH));
        IndexWriter indexWriter = createWriter(indexDir);

        ClassLoader classLoader = Application.class.getClassLoader();
        URL documentsUrl = classLoader.getResource("cran.all.1400");
        URL queriesUrl = classLoader.getResource("cran.qry");

        assert documentsUrl != null;
        assert queriesUrl != null;

        String rawCollection = Files.readString(Paths.get(documentsUrl.toURI()));
        String rawQueriesFile = Files.readString(Paths.get(queriesUrl.toURI()));

        Arrays.stream(rawCollection.split("(?=\\.I)"))
                .map(CranfieldParser::parseDocument)
                .map(CranfieldDocument::toLuceneDocument)
                .forEach(document -> addDocument(document, indexWriter));

        indexWriter.close();

        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(indexDir));

        String[] rawQueries = rawQueriesFile.split("(?=\\.I)");

        String trecEvalResults = Streams.mapWithIndex(
                        Arrays.stream(rawQueries),
                        (rawQuery, index) -> parseQuery(rawQuery, index + 1)
                )
                .map(cranfieldQuery -> query(cranfieldQuery, indexSearcher))
                .map(QueryResult::createTrecEvalResult)
                .collect(Collectors.joining("\n"));

        System.out.println(trecEvalResults);
        Files.writeString(Paths.get("../query_results.txt"), trecEvalResults);

        indexDir.close();
    }


    private static QueryResult query(CranfieldQuery cranfieldQuery, IndexSearcher indexSearcher) {
        try {
            QueryParser parser = new QueryParser("text", ANALYZER);
            Query query = parser.parse(QueryParser.escape(cranfieldQuery.text()));
            List<ScoreDoc> scoreDocs = Arrays.stream(indexSearcher.search(query, 50).scoreDocs).toList();

            return IntStream.range(0, scoreDocs.size())
                    .mapToObj(index -> {
                        try {
                            return new ScoredDocument(
                                    indexSearcher.storedFields().document(scoreDocs.get(index).doc).get("id"),
                                    scoreDocs.get(index).score,
                                    index + 1
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(collectingAndThen(
                            Collectors.toList(),
                            list -> new QueryResult(cranfieldQuery, list)
                    ));

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addDocument(Document document, IndexWriter iwriter) {
        try {
            iwriter.addDocument(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static IndexWriter createWriter(Directory indexDirectory) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(ANALYZER);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        return new IndexWriter(indexDirectory, config);
    }
}
