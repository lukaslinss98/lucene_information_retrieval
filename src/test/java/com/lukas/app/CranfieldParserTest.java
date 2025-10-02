package com.lukas.app;

import com.lukas.app.models.CranfieldDocument;
import com.lukas.app.models.CranfieldQuery;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CranfieldParserTest {

    @Test
    void givenRawDocument_ExpectObjectContainingIdAndAuthor() {
        // given
        String rawDocument = """
                                .I 1
                                .T
                        experimental investigation of the aerodynamics of a
                        wing in a slipstream .
                .A
                        brenckman,m.
                .B
                        j. ae. scs. 25, 1958, 324.
                                .W
                        experimental investigation of the aerodynamics of a
                        wing in a slipstream .
                        an experimental study of a wing in a propeller slipstream was
                        made in order to determine the spanwise distribution of the lift
                        increase due to slipstream at different angles of attack of the wing
                        and at different free stream to slipstream velocity ratios .  the
                        results were intended in part as an evaluation basis for different
                        theoretical treatments of this problem .
                                the comparative span loading curves, together with
                        supporting evidence, showed that a substantial part of  the lift increment
                        produced by the slipstream was due to a /destalling/ or
                        boundary-layer-control effect .  the integrated remaining lift
                        increment, after subtracting this destalling lift, was found to agree
                        well with a potential flow theory .
                        an empirical evaluation of the destalling effects was made for
                        the specific configuration of the experiment .
                """;
        // when
        CranfieldDocument cranfieldDocument = CranfieldParser.parseDocument(rawDocument);

        // then
        assertThat(cranfieldDocument)
                .isNotNull()
                .extracting(
                        CranfieldDocument::id,
                        CranfieldDocument::author,
                        CranfieldDocument::title
                )
                .contains(
                        1,
                        "brenckman,m.",
                        "experimental investigation of the aerodynamics of a wing in a slipstream ."
                );
    }

    @Test
    void givenRawQuery_ExpectObjectContainingIdAndText() {
        // given
        String rawQuery = """
                .I 001
                .W
                what similarity laws must be obeyed when constructing aeroelastic models
                of heated high speed aircraft .
                """;
        // when
        CranfieldQuery query = CranfieldParser.parseQuery(rawQuery);

        // then
        assertThat(query)
                .isNotNull()
                .extracting(
                        CranfieldQuery::id,
                        CranfieldQuery::text
                )
                .contains(
                        1,
                        "what similarity laws must be obeyed when constructing aeroelastic models of heated high speed aircraft ."
                );
    }
}