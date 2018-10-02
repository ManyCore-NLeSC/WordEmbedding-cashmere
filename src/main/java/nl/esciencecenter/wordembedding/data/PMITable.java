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

    public double getPMI(String wordOne, String wordTwo)
    {
        return log2((pairs.getPairOccurrences(wordOne, wordTwo) / pairs.getTotalPairs()) / ((vocabulary.getWord(wordOne).getOccurrences() / (vocabulary.getOccurrences() - vocabulary.getWord("</s>").getOccurrences())) * ((vocabulary.getWord(wordTwo).getOccurrences() / (vocabulary.getOccurrences() - vocabulary.getWord("</s>").getOccurrences())))));
    }

    private double log2(double x)
    {
        return Math.log(x) / Math.log(2);
    }
}