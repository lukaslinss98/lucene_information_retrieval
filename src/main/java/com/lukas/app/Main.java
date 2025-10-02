package com.lukas.app;

import com.lukas.app.models.CranfieldDocument;
import com.lukas.app.models.CranfieldQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    private static final Analyzer ANALYZER = new StandardAnalyzer();
    private static final String INDEX_DIRECTORY_PATH = "../index";

    public static void main(String[] args) throws URISyntaxException, IOException {
        Directory indexDir = FSDirectory.open(Paths.get(INDEX_DIRECTORY_PATH));
        IndexWriter indexWriter = createWriter(indexDir);

        ClassLoader classLoader = Main.class.getClassLoader();
        URL documentsUrl = classLoader.getResource("cran.all.1400");
        URL queriesUrl = classLoader.getResource("cran.qry");

        assert documentsUrl != null;
        assert queriesUrl != null;

        String rawCollection = Files.readString(Paths.get(documentsUrl.toURI()));
        String rawQueries = Files.readString(Paths.get(queriesUrl.toURI()));

        Arrays.stream(rawCollection.split("(?=\\.I)"))
                .map(CranfieldParser::parseDocument)
                .map(Main::toLuceneDocument)
                .forEach(document -> addDocument(document, indexWriter));

        DirectoryReader directoryReader = DirectoryReader.open(indexDir);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        String trecEvalResults = Arrays.stream(rawQueries.split("(?=\\.I)"))
                .map(CranfieldParser::parseQuery)
                .map(cranfieldQuery -> query(cranfieldQuery, indexSearcher))
                .map(entry -> createResultStringsForQuery(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining());

        System.out.println(trecEvalResults);

        indexWriter.close();
        indexDir.close();
    }

    private static String createResultStringsForQuery(CranfieldQuery query, TopDocs topDocs) {
        // format: query_id Q0 doc_id rank score run_name
        return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> "%s Q0 %s %s %s myrun".formatted(
                        query.id(),
                        "doc_id",
                        "rank",
                        scoreDoc.score)
                ).collect(Collectors.joining("\n"));
    }

    private static Map.Entry<CranfieldQuery, TopDocs> query(CranfieldQuery cranfieldQuery, IndexSearcher indexSearcher) {
//        try {
//            QueryParser parser = new QueryParser("text", ANALYZER);
//            Query query = parser.parse(QueryParser.escape(cranfieldQuery.text()));
//            TopDocs topDocs = indexSearcher.search(query, 50);
//            return new AbstractMap.SimpleEntry<>(cranfieldQuery, topDocs);
            return null;
//        } catch (IOException | ParseException e ) {
//            throw new RuntimeException(e);
//        }
    }

    private static void addDocument(Document document, IndexWriter iwriter) {
        try {
            iwriter.addDocument(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Document toLuceneDocument(CranfieldDocument cranfieldDocument) {
        Document document = new Document();
        document.add(new TextField("id", cranfieldDocument.id().toString(), Field.Store.YES));
        document.add(new TextField("title", cranfieldDocument.title(), Field.Store.YES));
        document.add(new TextField("author", cranfieldDocument.author(), Field.Store.YES));
        document.add(new TextField("text", cranfieldDocument.text(), Field.Store.YES));

        return document;
    }

    private static IndexWriter createWriter(Directory indexDirectory) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(ANALYZER);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        return new IndexWriter(indexDirectory, config);
    }
}
