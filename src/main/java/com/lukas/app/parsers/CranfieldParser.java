package com.lukas.app.parsers;

import com.lukas.app.models.CranfieldDocument;
import com.lukas.app.models.CranfieldQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CranfieldParser {

    public static CranfieldDocument parseDocument(String rawDocument) {
        Integer id = null;
        String title = null;
        String bibliography = null;
        String author = null;
        String text = null;

        Pattern fieldPattern = Pattern.compile("\\.(?<field>[A-Z])\\s+(?<value>.*?(?=\\.[A-Z]|\\z))", Pattern.DOTALL);
        Matcher matcher = fieldPattern.matcher(rawDocument);

        while (matcher.find()) {
            String fieldType = matcher.group(1);
            String fieldContent = matcher.group(2).replaceAll("\\s+", " ").trim();


            switch (fieldType) {
                case "I" -> id = Integer.parseInt(fieldContent);
                case "T" -> title = fieldContent;
                case "B" -> bibliography = fieldContent;
                case "A" -> author = fieldContent;
                case "W" -> text = fieldContent;
            }
        }
        return new CranfieldDocument(
                id,
                title,
                bibliography,
                author,
                text
        );
    }

    public static CranfieldQuery parseQuery(String rawQuery, long indexOfQuery) {
        Pattern fieldPattern = Pattern.compile("(?m)^\\.W\\s*\\R([\\s\\S]*)");
        Matcher matcher = fieldPattern.matcher(rawQuery);
        if (matcher.find()) {
            String queryText = matcher.group(1).replaceAll("\\s+", " ").trim();
            return new CranfieldQuery(indexOfQuery, queryText);
        }
        throw new IllegalArgumentException("Invalid Query format");
    }
}
