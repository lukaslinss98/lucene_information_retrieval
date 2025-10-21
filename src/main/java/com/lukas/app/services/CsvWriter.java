package com.lukas.app.services;

import com.lukas.app.models.TrecEvalResult;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class CsvWriter {
    public static void writeResultsToCsv(List<TrecEvalResult> results, Path path) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("analyzer,similarity,map,recall@100,P@5\n");

        for (TrecEvalResult r : results) {
            sb.append("%s,%s,%.4f,%.4f,%.4f\n".formatted(
                    r.usedAnalyzer().getClass().getSimpleName(),
                    r.usedSimilarity().getClass().getSimpleName(),
                    r.meanAveragePrecision(),
                    r.recall(),
                    r.precisionAtFive()
            ));
        }

        Files.writeString(path, sb.toString());
    }
}
