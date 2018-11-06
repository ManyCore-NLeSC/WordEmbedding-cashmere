package nl.esciencecenter.wordembedding.data;

import java.util.LinkedHashMap;

public class WordPairs
{
    private int windowSize;
    private final String separator = "_<%%>_";
    private int occurrences;
    private LinkedHashMap<String, Integer> coOccurrences;

    public WordPairs()
    {
        this.coOccurrences = new LinkedHashMap<>();
        this.occurrences = 0;
    }

    public void setWindowSize(int window)
    {
        this.windowSize = window;
    }

    public int getWindowSize()
    {
        return this.windowSize;
    }

    public int getTotalPairs()
    {
        return this.occurrences;
    }

    public void addPair(String wordOne, String wordTwo)
    {
        if ( coOccurrences.containsKey(wordOne + separator + wordTwo) )
        {
            coOccurrences.put(wordOne + separator + wordTwo, coOccurrences.get(wordOne + separator + wordTwo) + 1);
        }
        else if ( coOccurrences.containsKey(wordTwo + separator + wordOne) )
        {
            coOccurrences.put(wordTwo + separator + wordOne, coOccurrences.get(wordTwo + separator + wordOne) + 1);
        }
        else
        {
            coOccurrences.put(wordOne + separator + wordTwo, 1);
        }
        occurrences++;
    }

    public int getPairOccurrences(String wordOne, String wordTwo)
    {
        Integer pairOccurrences = coOccurrences.get(wordOne + separator + wordTwo);
        if ( pairOccurrences == null )
        {
            pairOccurrences = coOccurrences.get(wordTwo + separator + wordOne);
        }
        if ( pairOccurrences == null )
        {
            return 0;
        }
        else
        {
            return pairOccurrences;
        }
    }
}
