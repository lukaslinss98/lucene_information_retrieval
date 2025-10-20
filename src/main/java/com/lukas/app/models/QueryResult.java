package com.lukas.app.models;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record QueryResult(
        CranfieldQuery query,
        List<ScoredDocument> scoredDocuments
) {

    public String asTrecEvalResult() {
        // format: query_id Q0 doc_id rank score run_name
        String runId = UUID.randomUUID().toString().substring(0, 5);
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
