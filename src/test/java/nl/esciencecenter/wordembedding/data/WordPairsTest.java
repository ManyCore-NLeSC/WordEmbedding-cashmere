package nl.esciencecenter.wordembedding.data;

import nl.esciencecenter.wordembedding.utilities.LearnWordPairs;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class WordPairsTest {
    private final String foxSentence = "the quick brown fox jumps over the lazy dog";
    private final String kingSentence = "a man a bearded king is a man";

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
        populateVocabulary(vocabulary, foxSentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, foxSentence.split("[ \t]+"));
        assertEquals(16, pairs.getUniquePairs());
        pairs = new WordPairs(1);
        vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, kingSentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, kingSentence.split("[ \t]+"));
        assertEquals(10, pairs.getUniquePairs());
    }

    @Test
    public void getTotalPairs()
    {
        WordPairs pairs = new WordPairs(1);
        Vocabulary vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, foxSentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, foxSentence.split("[ \t]+"));
        assertEquals(16, pairs.getTotalPairs());
        pairs = new WordPairs(1);
        vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, kingSentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, kingSentence.split("[ \t]+"));
        assertEquals(14, pairs.getTotalPairs());
    }

    @Test
    public void addPair()
    {
        WordPairs pairs = new WordPairs(1);
        pairs.addPair("the", "king");
        assertEquals(1, pairs.getPairOccurrences("the", "king"));
        assertEquals(0, pairs.getPairOccurrences("king", "the"));
        assertEquals(1, pairs.getTotalPairs());
        assertEquals(1, pairs.getSingletonOccurrences("the"));
        assertEquals(0, pairs.getSingletonOccurrences("king"));
        assertEquals(1, pairs.getUniquePairs());
        pairs.addPair("king", "the");
        assertEquals(1, pairs.getPairOccurrences("the", "king"));
        assertEquals(1, pairs.getPairOccurrences("king", "the"));
        assertEquals(2, pairs.getTotalPairs());
        assertEquals(1, pairs.getSingletonOccurrences("the"));
        assertEquals(1, pairs.getSingletonOccurrences("king"));
        assertEquals(2, pairs.getUniquePairs());
    }

    @Test
    public void getPairOccurrences()
    {
        WordPairs pairs = new WordPairs(1);
        Vocabulary vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, foxSentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, foxSentence.split("[ \t]+"));
        assertEquals(0, pairs.getPairOccurrences("the", "fox"));
        assertEquals(1, pairs.getPairOccurrences("the", "lazy"));
        assertEquals(1, pairs.getPairOccurrences("lazy", "the"));
        pairs = new WordPairs(1);
        vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, kingSentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, kingSentence.split("[ \t]+"));
        assertEquals(3, pairs.getPairOccurrences("a", "man"));
        assertEquals(0, pairs.getPairOccurrences("king", "man"));
        assertEquals(1, pairs.getPairOccurrences("is", "king"));
    }

    @Test
    public void getOccurrences()
    {
        WordPairs pairs = new WordPairs(1);
        Vocabulary vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, foxSentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, foxSentence.split("[ \t]+"));
        assertEquals(0, pairs.getSingletonOccurrences("king"));
        assertEquals(3, pairs.getSingletonOccurrences("the"));
        assertEquals(1, pairs.getSingletonOccurrences("dog"));
        pairs = new WordPairs(1);
        vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, kingSentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, kingSentence.split("[ \t]+"));
        assertEquals(0, pairs.getSingletonOccurrences("fox"));
        assertEquals(5, pairs.getSingletonOccurrences("a"));
        assertEquals(2, pairs.getSingletonOccurrences("bearded"));
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
