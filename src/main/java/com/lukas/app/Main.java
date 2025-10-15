package com.lukas.app;

import com.google.common.collect.Streams;
import com.lukas.app.models.CranfieldDocument;
import com.lukas.app.models.QueryResult;
import com.lukas.app.services.QueryService;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.lukas.app.CranfieldParser.parseQuery;

public class Main {
  private static final Analyzer ANALYZER = new WhitespaceAnalyzer(10);
  private static final String INDEX_DIRECTORY_PATH = "../index";

  public static void main(String[] args) throws URISyntaxException, IOException {

    Directory indexDir = FSDirectory.open(Paths.get(INDEX_DIRECTORY_PATH));
    IndexWriter indexWriter = createWriter(indexDir);
    IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(indexDir));
    QueryService queryService = new QueryService(indexSearcher, ANALYZER);

    ClassLoader classLoader = Main.class.getClassLoader();
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

    String[] rawQueries = rawQueriesFile.split("(?=\\.I)");

    String trecEvalResults = Streams.mapWithIndex(
        Arrays.stream(rawQueries),
        (rawQuery, index) -> parseQuery(rawQuery, index + 1))
        .map(queryService::query)
        .map(QueryResult::createTrecEvalResult)
        .collect(Collectors.joining("\n"));

    System.out.println(trecEvalResults);
    Files.writeString(Paths.get("../query_results.txt"), trecEvalResults);

    indexDir.close();
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
