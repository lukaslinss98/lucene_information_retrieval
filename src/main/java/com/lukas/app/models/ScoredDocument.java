package com.lukas.app.models;

public record ScoredDocument(
        String id,
        Float score,
        Integer rank
) {
}
