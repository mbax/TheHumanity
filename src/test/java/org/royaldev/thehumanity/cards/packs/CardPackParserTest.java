package org.royaldev.thehumanity.cards.packs;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
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
        final List<String> packNames = CardPackParser.getListOfCardPackNames(packArray, new String[0]);
        // Make sure they turn out correctly
        assertThat(packNames, hasItems(
            "OneWord",
            "With Spaces",
            "With Spaces, Round Two",
            "With \"Extra\" Quotes",
            "With 'Extra' Quotes, Part Two"
        ));
    }

    @Test
    public void testGetNameFromFileName() throws Exception {
        // The extension should be removed
        assertEquals(
            "My.Dog",
            CardPackParser.getNameFromFileName("My.Dog.cards")
        );
        assertEquals(
            "My.Dog",
            CardPackParser.getNameFromFileName("My.Dog.derpherp")
        );
    }
}
