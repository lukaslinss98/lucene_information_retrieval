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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryService {
    private final IndexSearcher indexSearcher;
    private final Analyzer analyzer;

    private QueryService(IndexSearcher indexSearcher, Analyzer analyzer) {
        this.indexSearcher = indexSearcher;
        this.analyzer = analyzer;
    }

    public static QueryService create(
            Analyzer analyzer,
            Similarity similarity,
            Directory indexDirectory
    ) {
        try {
            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(similarity);
            return new QueryService(indexSearcher, analyzer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public QueryResult query(CranfieldQuery cranfieldQuery) {
        QueryParser queryParser = createQueryParser(analyzer);
        String cleanedQuery = QueryParser.escape(cranfieldQuery.text());

        try {
            Query query = queryParser.parse(cleanedQuery);
            List<ScoreDoc> scoreDocs = List.of(indexSearcher.search(query, 50).scoreDocs);

            ArrayList<ScoredDocument> scoredDocuments = new ArrayList<>();

            for (int i = 0; i < scoreDocs.size(); i++) {
                ScoreDoc scoreDoc = scoreDocs.get(i);
                ScoredDocument scoredDocument = new ScoredDocument(
                        indexSearcher.storedFields().document(scoreDoc.doc).get("id"),
                        scoreDoc.score,
                        i + 1
                );
                scoredDocuments.add(scoredDocument);
            }
            return new QueryResult(cranfieldQuery, scoredDocuments);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static QueryParser createQueryParser(Analyzer analyzer) {
        Map<String, Float> boosts = Map.of(
                "text", 3.0f,
                "title", 1.5f,
                "author", 0.5f,
                "bibliography", 0.1f
        );
        return new MultiFieldQueryParser(
                new String[]{"text", "title", "author", "bibliography"},
                analyzer,
                boosts
        );
    }
}
