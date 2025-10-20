package com.lukas.app.services;

import com.lukas.app.models.CranfieldQuery;
import com.lukas.app.models.QueryResult;
import com.lukas.app.models.ScoredDocument;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class QueryService {
    private final IndexSearcher indexSearcher;
    private final Analyzer analyzer;

    private QueryService(IndexSearcher indexSearcher, Analyzer analyzer) {
        this.indexSearcher = indexSearcher;
        this.analyzer = analyzer;
    }

    public static QueryService create(Path indexPath, Analyzer analyzer, Similarity similarity) {
        try (Directory indexDirectory = FSDirectory.open(indexPath)) {
            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(similarity);
            return new QueryService(indexSearcher, analyzer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public QueryResult query(CranfieldQuery cranfieldQuery) {
        String[] fields = {"text", "title"};
        QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
        String cleanedQuery = QueryParser.escape(cranfieldQuery.text());

        try {

            Query query = parser.parse(cleanedQuery);
            List<ScoreDoc> scoreDocs = List.of(indexSearcher.search(query, 50).scoreDocs);

            ArrayList<ScoredDocument> scoredDocuments = new ArrayList<>();

            for (int i = 0; i < scoreDocs.size(); i++) {
                ScoreDoc scoreDoc = scoreDocs.get(i);
                ScoredDocument scoredDocument = new ScoredDocument(
                        indexSearcher.storedFields().document(scoreDoc.doc).get("id"),
                        scoreDoc.score,
                        i + 1);
                scoredDocuments.add(scoredDocument);
            }
            return new QueryResult(cranfieldQuery, scoredDocuments);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
