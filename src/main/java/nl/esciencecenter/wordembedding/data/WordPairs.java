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
            return 0;
        }
        return occurrences;
    }

    public Set<String> getPairOccurrences()
    {
        return pairOccurrences.keySet();
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

    public Set<String> getSingletonOccurrences()
    {
        return singletonOccurrences.keySet();
    }

    public void sort()
    {
        ArrayList<String> sortedPairs= new ArrayList<>(pairOccurrences.keySet());
        sortedPairs.sort((pairOne, pairTwo) -> pairOccurrences.get(pairTwo) - pairOccurrences.get(pairOne));
        LinkedHashMap<String, Integer> newOccurrences = new LinkedHashMap<>();
        for ( String occurrence : sortedPairs ){
            newOccurrences.put(occurrence, pairOccurrences.get(occurrence));
        }
        pairOccurrences = newOccurrences;
        sortedPairs= new ArrayList<>(singletonOccurrences.keySet());
        sortedPairs.sort((wordOne, wordTwo) -> singletonOccurrences.get(wordTwo) - singletonOccurrences.get(wordOne));
        newOccurrences = new LinkedHashMap<>();
        for ( String occurrence : sortedPairs ){
            newOccurrences.put(occurrence, singletonOccurrences.get(occurrence));
        }
        singletonOccurrences = newOccurrences;
    }
}
