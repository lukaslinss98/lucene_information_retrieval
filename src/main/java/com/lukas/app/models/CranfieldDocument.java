package com.lukas.app.models;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public record CranfieldDocument(
        Integer id,
        String title,
        String author,
        String text
) {
    public Document toLuceneDocument() {
        Document document = new Document();
        document.add(new StringField("id", id.toString(), Field.Store.YES));
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new TextField("author", author, Field.Store.YES));
        document.add(new TextField("text", text, Field.Store.YES));

        return document;
    }
}
