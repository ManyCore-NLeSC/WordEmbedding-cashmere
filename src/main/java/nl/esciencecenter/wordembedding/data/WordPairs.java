package nl.esciencecenter.wordembedding.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public class WordPairs
{
    private final int windowSize;
    private final String separator = "_<%%>_";
    private long totalPairOccurrences;
    private LinkedHashMap<String, Integer> pairOccurrences;
    private ArrayList<String> sortedPairs;
    private LinkedHashMap<String, Integer> singletonOccurrences;
    private ArrayList<String> sortedSingletons;

    public WordPairs(int window)
    {
        this.pairOccurrences = new LinkedHashMap<>();
        this.sortedPairs = new ArrayList<>();
        this.singletonOccurrences = new LinkedHashMap<>();
        this.sortedSingletons = new ArrayList<>();
        this.totalPairOccurrences = 0;
        this.windowSize = window;
    }

    public int getWindowSize()
    {
        return this.windowSize;
    }

    public String getSeparator()
    {
        return separator;
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
        if ( pairOccurrences.containsKey(wordOne + separator + wordTwo) )
        {
            pairOccurrences.put(wordOne + separator + wordTwo, pairOccurrences.get(wordOne + separator + wordTwo) + 1);
        }
        else
        {
            pairOccurrences.put(wordOne + separator + wordTwo, 1);
            sortedPairs.add(wordOne + separator + wordTwo);
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
            sortedSingletons.add(singleton);
        }
    }

    public long getPairOccurrences(String wordOne, String wordTwo)
    {
        Integer occurrences = pairOccurrences.get(wordOne + separator + wordTwo);
        if ( occurrences == null )
        {
            return 0;
        }
        return occurrences;
    }

    public Set<String> getPairs()
    {
        return pairOccurrences.keySet();
    }

    public ArrayList<String> getSortedPairs()
    {
        return sortedPairs;
    }

    public long getSingletonOccurrences(String wordOne)
    {
        Integer occurrences = singletonOccurrences.get(wordOne);
        if ( occurrences == null )
        {
            return 0;
        }
        return occurrences;
    }

    public Set<String> getSingletons()
    {
        return singletonOccurrences.keySet();
    }

    public ArrayList<String> getSortedSingletons()
    {
        return sortedSingletons;
    }

    public void sort()
    {
        sortedPairs.sort((pairOne, pairTwo) -> pairOccurrences.get(pairTwo) - pairOccurrences.get(pairOne));
        sortedSingletons.sort((wordOne, wordTwo) -> singletonOccurrences.get(wordTwo) - singletonOccurrences.get(wordOne));
    }
}
