package nl.esciencecenter.wordembedding.data;

import nl.esciencecenter.wordembedding.utilities.LearnWordPairs;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class WordPairsTest {
    private final String sentence = "the quick brown fox jumps over the lazy dog";

    @Test
    public void getWindowSize()
    {
        WordPairs pairs = new WordPairs(5);
        assertEquals(5, pairs.getWindowSize());
    }

    @Test
    public void getUniquePairs()
    {
        WordPairs pairs = new WordPairs(1);
        Vocabulary vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, sentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, sentence.split("[ \t]+"));
        assertEquals(8, pairs.getUniquePairs());
    }

    @Test
    public void getTotalPairs()
    {
        WordPairs pairs = new WordPairs(1);
        Vocabulary vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, sentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, sentence.split("[ \t]+"));
        assertEquals(16, pairs.getTotalPairs());
    }

    @Test
    public void addPair()
    {
        WordPairs pairs = new WordPairs(1);
        pairs.addPair("the", "king");
        assertEquals(1, pairs.getPairOccurrences("the", "king"));
        assertEquals(1, pairs.getPairOccurrences("king", "the"));
        assertEquals(1, pairs.getTotalPairs());
        assertEquals(1, pairs.getOccurrences("the"));
        assertEquals(1, pairs.getOccurrences("king"));
        assertEquals(1, pairs.getUniquePairs());
        pairs.addPair("king", "the");
        assertEquals(2, pairs.getPairOccurrences("the", "king"));
        assertEquals(2, pairs.getPairOccurrences("king", "the"));
        assertEquals(2, pairs.getTotalPairs());
        assertEquals(2, pairs.getOccurrences("the"));
        assertEquals(2, pairs.getOccurrences("king"));
        assertEquals(1, pairs.getUniquePairs());
    }

    @Test
    public void getPairOccurrences()
    {
        WordPairs pairs = new WordPairs(1);
        Vocabulary vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, sentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, sentence.split("[ \t]+"));
        assertEquals(0, pairs.getPairOccurrences("the", "fox"));
        assertEquals(2, pairs.getPairOccurrences("the", "lazy"));
        assertEquals(2, pairs.getPairOccurrences("lazy", "the"));
    }

    @Test
    public void getOccurrences()
    {
        WordPairs pairs = new WordPairs(1);
        Vocabulary vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, sentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, sentence.split("[ \t]+"));
        assertEquals(0, pairs.getOccurrences("king"));
        assertEquals(6, pairs.getOccurrences("the"));
        assertEquals(2, pairs.getOccurrences("dog"));
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
