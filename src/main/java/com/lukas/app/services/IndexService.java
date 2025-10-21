package com.lukas.app.services;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.List;

public class IndexService {
    private final Analyzer analyzer;
    private final Similarity similarity;
    private final Directory indexDirectory;

    public IndexService(
            Analyzer analyzer,
            Similarity similarity,
            Directory directory
    ) {
        this.analyzer = analyzer;
        this.similarity = similarity;
        this.indexDirectory = directory;
    }

    public void addDocuments(List<Document> documents) {
        try  {
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
