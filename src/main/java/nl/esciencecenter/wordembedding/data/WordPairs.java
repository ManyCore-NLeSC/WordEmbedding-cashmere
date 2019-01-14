package nl.esciencecenter.wordembedding.data;

import java.util.LinkedHashMap;

public class WordPairs
{
    private final int windowSize;
    private final String separator = "_<%%>_";
    private long totalPairOccurrences;
    private LinkedHashMap<String, Integer> pairOccurrences;
    private LinkedHashMap<String, Integer> singletonOccurrences;

    public WordPairs(int window)
    {
        this.pairOccurrences = new LinkedHashMap<>();
        this.singletonOccurrences = new LinkedHashMap<>();
        this.totalPairOccurrences = 0;
        this.windowSize = window;
    }

    public int getWindowSize()
    {
        return this.windowSize;
    }

    public int getUniquePairs()
    {
        return pairOccurrences.size();
    }

    public long getTotalPairs()
    {
        return this.totalPairOccurrences;
    }

    public void addPair(String wordOne, String wordTwo)
    {
        this.addSingleton(wordOne);
        this.addSingleton(wordTwo);
        if ( pairOccurrences.containsKey(wordOne + separator + wordTwo) )
        {
            pairOccurrences.put(wordOne + separator + wordTwo, pairOccurrences.get(wordOne + separator + wordTwo) + 1);
        }
        else if ( pairOccurrences.containsKey(wordTwo + separator + wordOne) )
        {
            pairOccurrences.put(wordTwo + separator + wordOne, pairOccurrences.get(wordTwo + separator + wordOne) + 1);
        }
        else
        {
            pairOccurrences.put(wordOne + separator + wordTwo, 1);
        }
        totalPairOccurrences++;
    }

    private void addSingleton(String singleton)
    {
        if ( singletonOccurrences.containsKey(singleton) )
        {
            singletonOccurrences.put(singleton, singletonOccurrences.get(singleton) + 1);
        }
        else
        {
            singletonOccurrences.put(singleton, 1);
        }
    }

    public long getPairOccurrences(String wordOne, String wordTwo)
    {
        Integer occurrences = pairOccurrences.get(wordOne + separator + wordTwo);
        if ( occurrences == null )
        {
            occurrences = pairOccurrences.get(wordTwo + separator + wordOne);
        }
        if ( occurrences == null )
        {
            return 0;
        }
        return occurrences;
    }

    public long getOccurrences(String wordOne)
    {
        Integer occurrences = singletonOccurrences.get(wordOne);
        if ( occurrences == null )
        {
            return 0;
        }
        return occurrences;
    }
}
