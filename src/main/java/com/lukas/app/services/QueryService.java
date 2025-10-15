package com.lukas.app.services;

import com.lukas.app.models.CranfieldQuery;
import com.lukas.app.models.QueryResult;
import com.lukas.app.models.ScoredDocument;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryService {
    private final IndexSearcher indexSearcher;
    private final Analyzer analyzer;

    public QueryService(IndexSearcher indexSearcher, Analyzer analyzer) {
        this.indexSearcher = indexSearcher;
        this.analyzer = analyzer;
    }

    public QueryResult query(CranfieldQuery cranfieldQuery) {
        try {
            QueryParser parser = new QueryParser("text", analyzer);
            Query query = parser.parse(QueryParser.escape(cranfieldQuery.text()));
            List<ScoreDoc> scoreDocs = Arrays.stream(indexSearcher.search(query, 50).scoreDocs).toList();

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
