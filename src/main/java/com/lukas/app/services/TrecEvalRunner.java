package com.lukas.app.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TrecEvalRunner {
    public static void run(String trecEvalResults) throws IOException {
//      System.out.println(trecEvalResults);
        Files.writeString(Paths.get("../query_results.txt"), trecEvalResults);

        String qrelsPath = "/Users/lukas/dev/information_retrieval/lucene_information_retrieval/src/main/resources/cranqrel";
        String resultsPath = "/Users/lukas/dev/information_retrieval/query_results.txt";

        ProcessBuilder pb = new ProcessBuilder(
                "trec_eval",
                qrelsPath,
                resultsPath
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Read output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
