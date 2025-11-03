package com.lukas.app.services;

import com.lukas.app.models.AnalyzerSimilarityScore;
import com.lukas.app.models.TrecEvalResult;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrecEvalRunner {

    public static TrecEvalResult run(AnalyzerSimilarityScore analyzerSimilarityScore) {
        try {
            Path qrelsPath = Paths.get("/Users/lukas/dev/information_retrieval/lucene_information_retrieval/src/main/resources/QRelsCorrectedforTRECeval");
            Path resultsPath = Paths.get("../query_results.txt");
            Files.writeString(resultsPath, analyzerSimilarityScore.score());

            ProcessBuilder pb = new ProcessBuilder(
                    "trec_eval",
                    "-m", "map",
                    "-m", "recall.5,10,20",
                    "-m", "P.5,10,20",
                    qrelsPath.toString(),
                    resultsPath.toString()
            );

            pb.redirectErrorStream(true);

            Map<String, Float> metrics = new HashMap<>();

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

            return new TrecEvalResult(
                    analyzerSimilarityScore.analyzer(),
                    analyzerSimilarityScore.similarity(),
                    metrics.getOrDefault("map", 0f),
                    metrics.getOrDefault("recall_5", 0f),
                    metrics.getOrDefault("recall_10", 0f),
                    metrics.getOrDefault("recall_20", 0f),
                    metrics.getOrDefault("P_5", 0f),
                    metrics.getOrDefault("P_10", 0f),
                    metrics.getOrDefault("P_20", 0f)
            );
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
