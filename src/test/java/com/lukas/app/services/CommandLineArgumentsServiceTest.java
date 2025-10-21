package com.lukas.app.services;

import com.lukas.app.models.CommandLineArguments;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommandLineArgumentsServiceTest {


    @Test
    void receiveAnalyzerFlag_ExpectObjWithEnglishAnalyzer() {
        // given
        List<String> arguments = List.of("--analyzers", "english,standard");

        // when
        CommandLineArguments commandLineArguments = CommandLineArgumentsService.parseArgs(arguments);

        // then
        assertThat(commandLineArguments)
                .extracting(CommandLineArguments::analyzers)
                .satisfies(analyzers -> {
                    assertThat(analyzers).hasSize(2);
                    assertThat(analyzers.get(0)).isInstanceOf(EnglishAnalyzer.class);
                    assertThat(analyzers.get(1)).isInstanceOf(StandardAnalyzer.class);

                });
    }

    @Test
    void receiveSimilarityFlag_ExpectObjWithClassicSimilarity() {
        // given
        List<String> arguments = List.of("--similarities", "classic,bm25");

        // when
        CommandLineArguments commandLineArguments = CommandLineArgumentsService.parseArgs(arguments);

        // then
        assertThat(commandLineArguments)
                .extracting(CommandLineArguments::similarities)
                .satisfies(similarities -> {
                    assertThat(similarities).hasSize(2);
                    assertThat(similarities.get(0)).isInstanceOf(ClassicSimilarity.class);
                    assertThat(similarities.get(1)).isInstanceOf(BM25Similarity.class);

                });
    }

    @Test
    void receiveSimilaritiesAndAnalyzerFlags_ExpectObjWithTwoLists() {
        // given
        List<String> arguments = List.of("--similarities", "classic,bm25", "--analyzers", "english,standard");

        // when
        CommandLineArguments commandLineArguments = CommandLineArgumentsService.parseArgs(arguments);

        // then
        assertThat(commandLineArguments)
                .satisfies(args -> {
                    List<Analyzer> analyzers = args.analyzers();
                    List<Similarity> similarities = args.similarities();

                    assertThat(args.analyzers()).hasSize(2);
                    assertThat(args.similarities()).hasSize(2);
                    assertThat(similarities.get(0)).isInstanceOf(ClassicSimilarity.class);
                    assertThat(similarities.get(1)).isInstanceOf(BM25Similarity.class);

                    assertThat(analyzers.get(0)).isInstanceOf(EnglishAnalyzer.class);
                    assertThat(analyzers.get(1)).isInstanceOf(StandardAnalyzer.class);
                });
    }

}