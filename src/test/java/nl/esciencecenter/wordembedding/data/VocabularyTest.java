package nl.esciencecenter.wordembedding.data;

import nl.esciencecenter.wordembedding.utilities.ReduceVocabulary;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class VocabularyTest {
    private final String foxSentence = "the quick brown fox jumps over the lazy dog";
    private final String kingSentence = "a man a bearded king is a man";
    private final String stringOne = "common";
    private final String stringTwo = "word";

    @Test
    public void size() {
        Vocabulary vocabularyOne = new Vocabulary();

        vocabularyOne.setMaxSize(13);
        assertEquals(13, vocabularyOne.getMaxSize());
    }

    @Test
    public void threshold() {
        Vocabulary vocabularyOne = new Vocabulary();
        Vocabulary vocabularyTwo = new Vocabulary(7);

        assertEquals(0, vocabularyOne.getOccurrenceThreshold());
        assertEquals(7, vocabularyTwo.getOccurrenceThreshold());
        vocabularyOne.setOccurrenceThreshold(14);
        assertEquals(14, vocabularyOne.getOccurrenceThreshold());
    }

    @Test
    public void getOccurrences()
    {
        long occurrences = 0;
        Vocabulary vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, foxSentence.split("[ \t]+"));
        for ( Word word : vocabulary.getWords() )
        {
            occurrences += word.getOccurrences();
        }
        assertEquals(9, vocabulary.getOccurrences());
        assertEquals(9, occurrences);
        assertEquals(2, vocabulary.getMaxWordOccurrences());
        occurrences = 0;
        vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, kingSentence.split("[ \t]+"));
        for ( Word word : vocabulary.getWords() )
        {
            occurrences += word.getOccurrences();
        }
        assertEquals(8, vocabulary.getOccurrences());
        assertEquals(8, occurrences);
        assertEquals(3, vocabulary.getMaxWordOccurrences());
    }

    @Test
    public void words() {
        Word wordOne = new Word(stringOne, 3);
        Word wordTwo = new Word(stringTwo);
        Vocabulary vocabularyOne = new Vocabulary();

        vocabularyOne.setOccurrenceThreshold(2);
        vocabularyOne.addWord(wordOne);
        vocabularyOne.addWord(wordTwo);
        assertEquals(2, vocabularyOne.getNrWords());
        assertEquals(stringOne, vocabularyOne.getWord(stringOne).getWord());
        assertEquals(stringTwo, vocabularyOne.getWord(stringTwo).getWord());
        assertEquals(2, vocabularyOne.getWords().size());
        ReduceVocabulary.reduce(vocabularyOne);
        assertEquals(3, vocabularyOne.getOccurrenceThreshold());
        assertEquals(stringOne, vocabularyOne.getWord(stringOne).getWord());
        assertEquals(null, vocabularyOne.getWord(stringTwo));
        assertEquals(1, vocabularyOne.getWords().size());
    }

    @Test
    public void testProbabilities()
    {
        float probability = 0;
        Vocabulary vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, kingSentence.split("[ \t]+"));
        assertEquals(0.125, (float)(vocabulary.getWord("king").getOccurrences()) / (float)(vocabulary.getOccurrences()), 0.000001);
        for ( Word word : vocabulary.getWords() )
        {
            probability += (float)(word.getOccurrences()) / (float)(vocabulary.getOccurrences());
        }
        assertEquals(1.0, probability, 0.000001);
    }

    private void populateVocabulary(Vocabulary vocabulary, String [] values)
    {
        for ( int word = 0; word < values.length; word++ )
        {
            vocabulary.addWord(values[word]);
        }
        vocabulary.sort();
    }
}
