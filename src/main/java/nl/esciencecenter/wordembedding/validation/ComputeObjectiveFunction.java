package nl.esciencecenter.wordembedding.validation;

import nl.esciencecenter.wordembedding.data.*;
import nl.esciencecenter.wordembedding.math.DotProduct;
import nl.esciencecenter.wordembedding.math.Negate;
import nl.esciencecenter.wordembedding.math.Sigmoid;

public class ComputeObjectiveFunction {
    public static double objectiveFunctionWord2Vec(Vocabulary vocabulary, WordPairs pairs, WordEmbedding words, WordEmbedding contexts, int k)
    {
        double globalObjective = 0;
        for ( Word wordOne : vocabulary.getWords() )
        {
            if ( wordOne.getWord().equals("</s>") )
            {
                continue;
            }
            for ( Word wordTwo : vocabulary.getWords() )
            {
                if ( wordTwo.getWord().equals("</s>") )
                {
                    continue;
                }
                double localObjective = pairs.getPairOccurrences(wordOne.getWord(), wordTwo.getWord());
                localObjective *= Math.log(Sigmoid.compute(DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord()))));
                localObjective += k * pairs.getOccurrences(wordOne.getWord()) * ((float)(pairs.getOccurrences(wordTwo.getWord())) / (float)(pairs.getTotalPairs())) * Math.log(Sigmoid.compute(DotProduct.compute(Negate.compute(words.getWordCoordinates(wordOne.getWord())), contexts.getWordCoordinates(wordTwo.getWord()))));
                globalObjective += localObjective;
            }
        }
        return globalObjective;
    }

    public static double deviationFromOptimalWord2Vec(Vocabulary vocabulary, WordPairs pairs, PMITable pmiTable, WordEmbedding words, WordEmbedding contexts, int k)
    {
        double word2vecObjective = 0;
        double pmiObjective = 0;
        for ( Word wordOne : vocabulary.getWords() )
        {
            if ( wordOne.getWord().equals("</s>") )
            {
                continue;
            }
            for ( Word wordTwo : vocabulary.getWords() )
            {
                if ( wordTwo.getWord().equals("</s>") )
                {
                    continue;
                }
                double localObjective = pairs.getPairOccurrences(wordOne.getWord(), wordTwo.getWord());
                localObjective *= Math.log(Sigmoid.compute(pmiTable.getPMI(wordOne.getWord(), wordTwo.getWord()) - Math.log(k)));
                localObjective += k * pairs.getOccurrences(wordOne.getWord()) * ((float)(pairs.getOccurrences(wordTwo.getWord())) / (float)(pairs.getTotalPairs())) * Math.log(Sigmoid.compute(-(pmiTable.getPMI(wordOne.getWord(), wordTwo.getWord()) - Math.log(k))));
                if ( Double.isFinite(localObjective) )
                {
                    pmiObjective += localObjective;
                    localObjective = pairs.getPairOccurrences(wordOne.getWord(), wordTwo.getWord());
                    localObjective *= Math.log(Sigmoid.compute(DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord()))));
                    localObjective += k * pairs.getOccurrences(wordOne.getWord()) * ((float)(pairs.getOccurrences(wordTwo.getWord())) / (float)(pairs.getTotalPairs())) * Math.log(Sigmoid.compute(DotProduct.compute(Negate.compute(words.getWordCoordinates(wordOne.getWord())), contexts.getWordCoordinates(wordTwo.getWord()))));
                    word2vecObjective += localObjective;
                }
            }
        }
        return (word2vecObjective - pmiObjective) / pmiObjective;
    }
}
