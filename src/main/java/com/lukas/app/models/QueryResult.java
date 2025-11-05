package com.lukas.app.models;

import java.util.List;
import java.util.stream.Collectors;

public record QueryResult(
        CranfieldQuery query,
        List<ScoredDocument> scoredDocuments
) {

    public String toTrecRunFormat(String runId) {
        return scoredDocuments.stream()
                .map(scoredDocument -> "%s Q0 %s %s %s %s".formatted(
                        query.id(),
                        scoredDocument.id(),
                        scoredDocument.rank(),
                        scoredDocument.score(),
                        runId
                )).collect(Collectors.joining("\n"));
    }
}
