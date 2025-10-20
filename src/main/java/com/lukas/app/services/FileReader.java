package com.lukas.app.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileReader {
    public static String readFiles(String filePath) {
        try (InputStream in = FileReader.class.getResourceAsStream(filePath)) {
            if (in == null) throw new FileNotFoundException("Resource not found: %s".formatted(filePath));

            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
