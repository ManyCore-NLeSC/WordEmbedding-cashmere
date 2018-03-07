package nl.esciencecenter.wordembedding.data;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class WordTest {
    private final String stringOne = "common";
    private final String stringTwo = "word";

    @Test
    public void occurrences() {
        Word wordOne = new Word(stringOne);
        Word wordTwo = new Word(stringTwo, 4);

        assertEquals(0, wordOne.getOccurrences());
        assertEquals(4, wordTwo.getOccurrences());
        wordOne.incrementOccurrences();
        wordTwo.incrementOccurrences();
        assertEquals(1, wordOne.getOccurrences());
        assertEquals(5, wordTwo.getOccurrences());
    }

    @Test
    public void word() {
        Word wordOne = new Word(stringOne);
        Word wordTwo = new Word(stringTwo);

        assertEquals(stringOne, wordOne.getWord());
        assertEquals(stringTwo, wordTwo.getWord());
    }
}
