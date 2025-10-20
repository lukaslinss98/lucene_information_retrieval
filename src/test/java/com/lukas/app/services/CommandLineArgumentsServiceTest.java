package com.lukas.app.services;

import com.lukas.app.models.CommandLineArguments;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommandLineArgumentsServiceTest {


    @Test
    void receiveAnalyzerFlag_ExpectObjWithEnglishAnalyzer() {
        // given
        List<String> arguments = List.of("--analyzer", "english");

        // when
        CommandLineArguments commandLineArguments = CommandLineArgumentsService.parseArgs(arguments);

        // then
        assertThat(commandLineArguments)
                .extracting(args -> args.analyzer().get())
                .isInstanceOf(EnglishAnalyzer.class);
    }

    @Test
    void receiveSimilarityFlag_ExpectObjWithClassicSimilarity() {
        // given
        List<String> arguments = List.of("--similarity", "classic");

        // when
        CommandLineArguments commandLineArguments = CommandLineArgumentsService.parseArgs(arguments);

        // then
        assertThat(commandLineArguments)
                .extracting(args -> args.similarity().get())
                .isInstanceOf(ClassicSimilarity.class);
    }

    @Test
    void receiveSimilarityAndAnalyzerFlag_ExpectObjWithClassicSimilarityAndEnglishAnalyzer() {
        // given
        List<String> arguments = List.of("--analyzer", "english", "--similarity", "classic");

        // when
        CommandLineArguments commandLineArguments = CommandLineArgumentsService.parseArgs(arguments);

        // then
        assertThat(commandLineArguments)
                .extracting(
                        args -> args.analyzer().get().getClass(),
                        args -> args.similarity().get().getClass()
                )
                .containsExactly(
                        EnglishAnalyzer.class,
                        ClassicSimilarity.class
                );
    }

}