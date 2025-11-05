package com.lukas.app.io;

import com.lukas.app.models.TrecEvalResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvWriter {
    public static void writeResultsToCsv(List<TrecEvalResult> results, Path path) {
        StringBuilder sb = new StringBuilder();
        sb.append("analyzer,similarity,map,recall@5,recall@10,recall@20,P@5,P@10,P@20\n");

        results.forEach(result -> {
            sb.append("%s,%s,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f\n".formatted(
                    result.usedAnalyzer().getClass().getSimpleName(),
                    result.usedSimilarity().getClass().getSimpleName(),
                    result.meanAveragePrecision(),
                    result.recallAtFive(),
                    result.recallAtTen(),
                    result.recallAtTwenty(),
                    result.precisionAtFive(),
                    result.precisionAtTen(),
                    result.precisionAtTwenty()
            ));
        });

        try {
            Files.writeString(path, sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
