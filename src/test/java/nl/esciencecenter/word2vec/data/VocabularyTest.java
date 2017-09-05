package nl.esciencecenter.word2vec.data;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class VocabularyTest {
    private final String stringOne = "common";
    private final String stringTwo = "word";

    @Test
    public void threshold() {
        Vocabulary vocabularyOne = new Vocabulary();
        Vocabulary vocabularyTwo = new Vocabulary(7);

        assertEquals(5, vocabularyOne.getOccurrenceThreshold().intValue());
        assertEquals(7, vocabularyTwo.getOccurrenceThreshold().intValue());
        vocabularyOne.setOccurrenceThreshold(14);
        assertEquals(14, vocabularyOne.getOccurrenceThreshold().intValue());
    }

    @Test
    public void words() {
        Word wordOne = new Word(stringOne, 3);
        Word wordTwo = new Word(stringTwo);
        Vocabulary vocabularyOne = new Vocabulary();

        vocabularyOne.setOccurrenceThreshold(2);
        vocabularyOne.addWord(wordOne);
        vocabularyOne.addWord(wordTwo);
        assertEquals(2, vocabularyOne.getNrWords().intValue());
        assertEquals(stringOne, vocabularyOne.getWord(stringOne).getWord());
        assertEquals(stringTwo, vocabularyOne.getWord(stringTwo).getWord());
        assertEquals(2, vocabularyOne.getWords().size());
        vocabularyOne.reduce();
        assertEquals(3, vocabularyOne.getOccurrenceThreshold().intValue());
        assertEquals(stringOne, vocabularyOne.getWord(stringOne).getWord());
        assertEquals(null, vocabularyOne.getWord(stringTwo));
        assertEquals(1, vocabularyOne.getWords().size());
    }
}
