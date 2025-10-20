package com.lukas.app.services;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class IndexService {
    private final Path indexPath;
    private final Analyzer analyzer;
    private final Similarity similarity;

    public IndexService(
            Path indexPath,
            Analyzer analyzer,
            Similarity similarity
    ) {
        this.indexPath = indexPath;
        this.analyzer = analyzer;
        this.similarity = similarity;
    }

    public void addDocuments(List<Document> documents) {
        try (Directory indexDirectory = FSDirectory.open(indexPath)) {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriterConfig.setSimilarity(similarity);
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);

            for (Document document : documents) {
                indexWriter.addDocument(document);
            }

            indexWriter.commit();
            indexWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
