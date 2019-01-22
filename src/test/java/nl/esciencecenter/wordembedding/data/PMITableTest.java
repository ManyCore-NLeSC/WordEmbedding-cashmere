package nl.esciencecenter.wordembedding.data;

import nl.esciencecenter.wordembedding.utilities.LearnWordPairs;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class PMITableTest {
    private final String kingSentence = "a man a bearded king is a man";

    @Test
    public void getPMI()
    {
        WordPairs pairs = new WordPairs(1);
        Vocabulary vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, kingSentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, kingSentence.split("[ \t]+"));
        PMITable pmiTable = new PMITable(pairs);
        assertEquals(1.4854268271702415, pmiTable.getPMI("a", "man"), 0.000001);
        assertEquals(1.8073549220576042, pmiTable.getPMI("king", "bearded"), 0.000001);
        assertEquals(true, Float.isInfinite(pmiTable.getPMI("is", "bearded")));
    }

    @Test
    public void getPPMI()
    {
        WordPairs pairs = new WordPairs(1);
        Vocabulary vocabulary = new Vocabulary();
        populateVocabulary(vocabulary, kingSentence.split("[ \t]+"));
        LearnWordPairs.learn(pairs, vocabulary, kingSentence.split("[ \t]+"));
        PMITable pmiTable = new PMITable(pairs);
        assertEquals(1.4854268271702415, pmiTable.getPPMI("a", "man"), 0.000001);
        assertEquals(1.8073549220576042, pmiTable.getPPMI("king", "bearded"), 0.000001);
        assertEquals(0.0, pmiTable.getPPMI("is", "bearded"), 0.000001);
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
