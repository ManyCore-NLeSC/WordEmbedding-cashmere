package nl.esciencecenter.wordembedding.data;

public class PMITable
{
    private final WordPairs pairs;

    public PMITable(WordPairs pairs)
    {
        this.pairs = pairs;
    }

    public float getPMI(String wordOne, String wordTwo)
    {
        return log2((float)(pairs.getPairOccurrences(wordOne, wordTwo) * pairs.getTotalPairs()) / (pairs.getOccurrences(wordOne) * pairs.getOccurrences(wordTwo)));
    }

    public float getPPMI(String wordOne, String wordTwo)
    {
        return Math.max(this.getPMI(wordOne, wordTwo), 0);
    }

    private float log2(float x)
    {
        return (float)(Math.log(x) / Math.log(2));
    }
}