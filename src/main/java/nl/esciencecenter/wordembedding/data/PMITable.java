package nl.esciencecenter.wordembedding.data;

public class PMITable
{
    private final Vocabulary vocabulary;
    private final WordPairs pairs;

    public PMITable(Vocabulary vocabulary, WordPairs pairs)
    {
        this.vocabulary = vocabulary;
        this.pairs = pairs;
    }

    public float getPMI(String wordOne, String wordTwo)
    {
        return Math.max(log2(((float)(pairs.getPairOccurrences(wordOne, wordTwo)) / pairs.getTotalPairs()) / (((float)(vocabulary.getWord(wordOne).getOccurrences()) / (vocabulary.getOccurrences() - vocabulary.getWord("</s>").getOccurrences())) * (((float)(vocabulary.getWord(wordTwo).getOccurrences()) / (vocabulary.getOccurrences() - vocabulary.getWord("</s>").getOccurrences()))))), 0.0f);
    }

    private float log2(float x)
    {
        return (float)(Math.log(x) / Math.log(2));
    }
}