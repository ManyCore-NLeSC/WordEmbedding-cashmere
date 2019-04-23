package nl.esciencecenter.wordembedding.data;

import nl.esciencecenter.wordembedding.math.DotProduct;
import nl.esciencecenter.wordembedding.math.Sigmoid;

public class ObjectiveFunction {
    private double pmi = 0;
    private double word2vec = 0;

    public double getPMI()
    {
        return pmi;
    }

    public double getWord2Vec()
    {
        return word2vec;
    }

    public double getPercentageOfDeviation()
    {
        return ((word2vec / pmi) - 1) * -100;
    }

    public void incrementPMI(PMITable pmiTable, String wordOne, String wordTwo, int k)
    {
        double localObjective = Math.log(Sigmoid.compute(pmiTable.getPMI(wordOne, wordTwo) - Math.log(k)));
        localObjective += k * Math.log(Sigmoid.compute(-(pmiTable.getPMI(wordOne, wordTwo) - Math.log(k))));
        pmi += localObjective;
    }

    public void incrementPMI(PMITable pmiTable, Vocabulary vocabulary, String wordOne, String wordTwo, int k)
    {
        double localObjective = Math.log(Sigmoid.compute(pmiTable.getPMI(wordOne, wordTwo) - Math.log(k)));
        localObjective += k * Math.log(Sigmoid.compute(-(pmiTable.getPMI(wordOne, vocabulary.getRandomWord()) - Math.log(k))));
        pmi += localObjective;
    }

    public void incrementWord2Vec(WordEmbedding words, WordEmbedding contexts, String wordOne, String wordTwo, int k)
    {
        double localObjective = Math.log(Sigmoid.compute(DotProduct.compute(words.getWordCoordinates(wordOne), contexts.getWordCoordinates(wordTwo))));
        localObjective += k * Math.log(Sigmoid.compute(-(DotProduct.compute(words.getWordCoordinates(wordOne), contexts.getWordCoordinates(wordTwo)))));
        word2vec += localObjective;
    }

    public void incrementWord2Vec(Vocabulary vocabulary, WordEmbedding words, WordEmbedding contexts, String wordOne, String wordTwo, int k)
    {
        double localObjective = Math.log(Sigmoid.compute(DotProduct.compute(words.getWordCoordinates(wordOne), contexts.getWordCoordinates(wordTwo))));
        localObjective += k * Math.log(Sigmoid.compute(-(DotProduct.compute(words.getWordCoordinates(wordOne), contexts.getWordCoordinates(vocabulary.getRandomWord())))));
        word2vec += localObjective;
    }
}
