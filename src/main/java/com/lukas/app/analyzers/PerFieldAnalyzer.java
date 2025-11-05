package com.lukas.app.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

import java.util.Map;

public class PerFieldAnalyzer {

    public static PerFieldAnalyzerWrapper create() {
        Map<String, Analyzer> perField = Map.of(
                "id", new KeywordAnalyzer(),
                "text", new CustomAnalyzer(),
                "bibliography", new KeywordAnalyzer(),
                "title", new EnglishAnalyzer(),
                "author", new KeywordAnalyzer()
        );
        return new PerFieldAnalyzerWrapper(new EnglishAnalyzer(), perField);
    }
}
