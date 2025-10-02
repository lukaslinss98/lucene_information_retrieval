package com.lukas.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CranfieldParser {

    public static CranfieldDocument parse(String rawDocument) {
        Integer id = null;
        String title = null;
        String author = null;
        String text = null;

        Pattern fieldPattern = Pattern.compile("\\.(?<field>[A-Z])\\s+(?<value>(?:.*?)(?=\\.[A-Z]|\\z))", Pattern.DOTALL);
        Matcher matcher = fieldPattern.matcher(rawDocument);

        while (matcher.find()) {
            String fieldType = matcher.group(1);  // The letter (I, T, A, B, W, etc.)
            String fieldContent = matcher.group(2).replaceAll("\\s+", " ").trim();  // The content


            switch (fieldType) {
                case "I" -> {
                    id = Integer.parseInt(fieldContent);
                }
                case "T" -> {
                    title = fieldContent;
                }
                case "A" -> {
                    author = fieldContent;
                }
                case "W" -> {
                    text = fieldContent;
                }
            }
        }
        return new CranfieldDocument(id, title, author, text);
    }
}
