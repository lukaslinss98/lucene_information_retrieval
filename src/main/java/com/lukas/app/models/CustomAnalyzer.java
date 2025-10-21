package com.lukas.app.models;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.util.List;

public class CustomAnalyzer extends Analyzer {

    private final CharArraySet stopWords;

    public CustomAnalyzer() {
        this.stopWords = new CharArraySet(
                List.of("a", "an", "the", "and", "or", "of", "to", "for", "with", "in", "on", "by", "is", "are", "was", "were"),
                true
        );
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(source);
        tokenStream = new StopFilter(tokenStream, stopWords);
        tokenStream = new PorterStemFilter(tokenStream);
        return new TokenStreamComponents(source, tokenStream);
    }
}
