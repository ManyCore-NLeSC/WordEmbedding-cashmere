package nl.esciencecenter.wordembedding.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

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
        String pair = wordOne + separator + wordTwo;
        if ( pairOccurrences.containsKey(pair) )
        {
            pairOccurrences.put(pair, pairOccurrences.get(pair) + 1);
        }
        else
        {
            pairOccurrences.put(pair, 1);
        }
        this.addSingleton(wordOne);
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

    public Set<String> getPairs()
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

    public Set<String> getSingletons()
    {
        return singletonOccurrences.keySet();
    }

    public void sort()
    {
        pairOccurrences = pairOccurrences.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,LinkedHashMap::new));
        singletonOccurrences= singletonOccurrences.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,LinkedHashMap::new));
    }
}
