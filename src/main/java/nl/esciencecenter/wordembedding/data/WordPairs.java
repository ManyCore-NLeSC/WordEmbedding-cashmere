package nl.esciencecenter.wordembedding.data;

import java.util.LinkedHashMap;

public class WordPairs
{
    private int windowSize;
    private final String separator = "_<%%>_";
    private long totalPairOccurrences;
    private LinkedHashMap<String, Integer> occurrences;

    public WordPairs()
    {
        this.occurrences = new LinkedHashMap<>();
        this.totalPairOccurrences = 0;
    }

    public void setWindowSize(int window)
    {
        this.windowSize = window;
    }

    public int getWindowSize()
    {
        return this.windowSize;
    }

    public long getTotalPairs()
    {
        return this.totalPairOccurrences;
    }

    public void addPair(String wordOne, String wordTwo)
    {
        this.addSingleton(wordOne);
        this.addSingleton(wordTwo);
        if ( occurrences.containsKey(wordOne + separator + wordTwo) )
        {
            occurrences.put(wordOne + separator + wordTwo, occurrences.get(wordOne + separator + wordTwo) + 1);
        }
        else if ( occurrences.containsKey(wordTwo + separator + wordOne) )
        {
            occurrences.put(wordTwo + separator + wordOne, occurrences.get(wordTwo + separator + wordOne) + 1);
        }
        else
        {
            occurrences.put(wordOne + separator + wordTwo, 1);
        }
        totalPairOccurrences++;
    }

    private void addSingleton(String singleton)
    {
        if ( occurrences.containsValue(singleton) )
        {
            occurrences.put(singleton, occurrences.get(singleton) + 1);
        }
        else
        {
            occurrences.put(singleton, 1);
        }
    }

    public long getPairOccurrences(String wordOne, String wordTwo)
    {
        Integer pairOccurrences = occurrences.get(wordOne + separator + wordTwo);
        if ( pairOccurrences == null )
        {
            pairOccurrences = occurrences.get(wordTwo + separator + wordOne);
        }
        if ( pairOccurrences == null )
        {
            return 0;
        }
        return pairOccurrences;
    }

    public long getOccurrences(String wordOne)
    {
        Integer singletonOccurrences = occurrences.get(wordOne);
        if ( singletonOccurrences == null )
        {
            return 0;
        }
        return singletonOccurrences;
    }
}
