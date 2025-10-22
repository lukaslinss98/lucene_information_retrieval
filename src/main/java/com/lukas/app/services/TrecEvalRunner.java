package com.lukas.app.services;

import com.lukas.app.models.TrecEvalResult;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrecEvalRunner {

    public static TrecEvalResult run(
            String trecEvalResults,
            Analyzer analyzer,
            Similarity similarity
    ) throws IOException {
        Path qrelsPath = Paths.get("/Users/lukas/dev/information_retrieval/lucene_information_retrieval/src/main/resources/cranqrel");
        Path resultsPath = Paths.get("../query_results.txt");
        Files.writeString(resultsPath, trecEvalResults);

        ProcessBuilder pb = new ProcessBuilder(
                "trec_eval", // has to be on PATH
                "-m", "map",
                "-m", "recall.5,10,20",
                "-m", "P.5,10,20",
                qrelsPath.toString(),
                resultsPath.toString()
        );

        pb.redirectErrorStream(true);

        Map<String, Float> metrics = new HashMap<>();

        try {
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                Pattern pattern = Pattern.compile("^(\\S+)\\s+all\\s+(\\S+)$");

                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line.trim());
                    if (matcher.find()) {
                        String metric = matcher.group(1);
                        float value = Float.parseFloat(matcher.group(2));
                        metrics.put(metric, value);
                    }
                }
            }

            process.waitFor();

        } catch (InterruptedException e) {
            throw new RuntimeException("trec_eval execution interrupted", e);
        }

        return new TrecEvalResult(
                analyzer,
                similarity,
                metrics.getOrDefault("map", 0f),
                metrics.getOrDefault("recall_5", 0f),
                metrics.getOrDefault("recall_10", 0f),
                metrics.getOrDefault("recall_20", 0f),
                metrics.getOrDefault("P_5", 0f),
                metrics.getOrDefault("P_10", 0f),
                metrics.getOrDefault("P_20", 0f)
        );
    }
}
