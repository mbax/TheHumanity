package org.royaldev.thehumanity.cards;

import org.junit.Test;
import org.royaldev.thehumanity.cards.packs.CardPackParser;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class CardPackParserTest {

    @Test
    public void testGetListOfCardPackNames() throws Exception {
        // Make up some pack names with expected command-line/argument syntax
        final String[] packArray = new String[]{
            "OneWord",
            "\"With Spaces\"",
            "'With Spaces, Round Two'",
            "\"With \\\"Extra\\\" Quotes\"",
            "'With \\'Extra\\' Quotes, Part Two'"
        };
        // Convert them
        final List<String> packNames = CardPackParser.getListOfCardPackNames(packArray);
        // Make sure they turn out correctly
        assertThat(packNames, hasItems(
            "OneWord",
            "With Spaces",
            "With Spaces, Round Two",
            "With \"Extra\" Quotes",
            "With 'Extra' Quotes, Part Two"
        ));
    }
}
