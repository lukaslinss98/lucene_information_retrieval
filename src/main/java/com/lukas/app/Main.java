package com.lukas.app;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        URL resource = Main.class.getClassLoader().getResource("cran.all.1400");
        assert resource != null;
        String collection = Files.readString(Paths.get(resource.toURI()));

        Analyzer analyzer = new StandardAnalyzer();

        Directory directory = FSDirectory.open(Paths.get("../index"));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        // Index opening mode
        // IndexWriterConfig.OpenMode.CREATE = create a new index
        // IndexWriterConfig.OpenMode.APPEND = open an existing index
        // IndexWriterConfig.OpenMode.CREATE_OR_APPEND = create an index if it
        // does not exist, otherwise it opens it
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        IndexWriter iwriter = new IndexWriter(directory, config);

        Arrays.stream(collection.split("(?=\\.I)"))
                .map(CranfieldParser::parse)
                .peek(System.out::println)
                .map(Main::toLuceneDocument)
                .forEach(document -> addDocument(document, iwriter));

        iwriter.close();
        directory.close();
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
}
