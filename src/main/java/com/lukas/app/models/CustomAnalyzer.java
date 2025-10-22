package com.lukas.app.models;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.util.List;

public class CustomAnalyzer extends Analyzer {

    private final CharArraySet stopWords;

    public CustomAnalyzer() {
        CharArraySet customStopWords = new CharArraySet(
                List.of("paper", "study", "report", "method", "result", "results",
                        "show", "shown", "shows", "present", "presented", "presents",
                        "discuss", "discussed", "discusses", "describe", "described", "describes"),
                true
        );
        customStopWords.addAll(EnglishAnalyzer.getDefaultStopSet());
        this.stopWords = customStopWords;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(source);
        tokenStream = new StopFilter(tokenStream, stopWords);
        tokenStream = new PorterStemFilter(tokenStream);
        ShingleFilter shingleFilter = new ShingleFilter(tokenStream, 2, 2);
        shingleFilter.setOutputUnigrams(true);
        return new TokenStreamComponents(source, tokenStream);
    }
}
