package nl.esciencecenter.wordembedding.validation;

import nl.esciencecenter.wordembedding.data.*;
import nl.esciencecenter.wordembedding.math.DotProduct;
import nl.esciencecenter.wordembedding.math.Negate;
import nl.esciencecenter.wordembedding.math.Sigmoid;

public class CompareObjectiveFunction {
    public static double objectiveFunctionWord2Vec(WordPairs pairs, WordEmbedding words, WordEmbedding contexts, int k, long maxPairs)
    {
        long pairsCounter = 0;
        double globalObjective = 0;
        for ( String pair : pairs.getPairs() )
        {
            if ( pairsCounter >= maxPairs )
            {
                break;
            }
            String [] pairWords = pair.split(pairs.getSeparator());
            double localObjective = pairs.getPairOccurrences(pairWords[0], pairWords[1]);
            localObjective *= Math.log(Sigmoid.compute(DotProduct.compute(words.getWordCoordinates(pairWords[0]), contexts.getWordCoordinates(pairWords[1]))));
            localObjective += k * pairs.getSingletonOccurrences(pairWords[0]) * ((float)(pairs.getSingletonOccurrences(pairWords[1])) / (float)(pairs.getTotalPairs())) * Math.log(Sigmoid.compute(DotProduct.compute(Negate.compute(words.getWordCoordinates(pairWords[0])), contexts.getWordCoordinates(pairWords[1]))));
            globalObjective += localObjective;
            pairsCounter++;
        }
        return globalObjective;
    }

    public static double deviationFromOptimalWord2Vec(WordPairs pairs, PMITable pmiTable, WordEmbedding words, WordEmbedding contexts, int k, long maxPairs)
    {
        long pairsCounter = 0;
        double word2vecObjective = 0;
        double pmiObjective = 0;
        for ( String pair : pairs.getSortedPairs() )
        {
            if ( pairsCounter >= maxPairs )
            {
                break;
            }
            String [] pairWords = pair.split(pairs.getSeparator());
            double localObjective = pairs.getPairOccurrences(pairWords[0], pairWords[1]);
            localObjective *= Math.log(Sigmoid.compute(pmiTable.getPMI(pairWords[0], pairWords[1]) - Math.log(k)));
            localObjective += k * pairs.getSingletonOccurrences(pairWords[0]) * ((float)(pairs.getSingletonOccurrences(pairWords[1])) / (float)(pairs.getTotalPairs())) * Math.log(Sigmoid.compute(-(pmiTable.getPMI(pairWords[0], pairWords[1]) - Math.log(k))));
            pmiObjective += localObjective;
            localObjective = pairs.getPairOccurrences(pairWords[0], pairWords[1]);
            localObjective *= Math.log(Sigmoid.compute(DotProduct.compute(words.getWordCoordinates(pairWords[0]), contexts.getWordCoordinates(pairWords[1]))));
            localObjective += k * pairs.getSingletonOccurrences(pairWords[0]) * ((float)(pairs.getSingletonOccurrences(pairWords[1])) / (float)(pairs.getTotalPairs())) * Math.log(Sigmoid.compute(DotProduct.compute(Negate.compute(words.getWordCoordinates(pairWords[0])), contexts.getWordCoordinates(pairWords[1]))));
            word2vecObjective += localObjective;
            pairsCounter++;
        }
        return (word2vecObjective - pmiObjective) / pmiObjective;
    }

    public static double distanceFromPMIWord2Vec(WordPairs pairs, PMITable pmiTable, WordEmbedding words, WordEmbedding contexts, int k, long maxPairs)
    {
        long pairsCounter = 0;
        double distance = 0;
        for ( String pair : pairs.getSortedPairs() )
        {
            if ( pairsCounter >= maxPairs )
            {
                break;
            }
            String [] pairWords = pair.split(pairs.getSeparator());
            distance += Math.pow(pmiTable.getPMI(pairWords[0], pairWords[1]) - Math.log(k) - DotProduct.compute(words.getWordCoordinates(pairWords[0]), contexts.getWordCoordinates(pairWords[1])), 2);
            pairsCounter++;
        }
        return Math.sqrt(distance) / pairsCounter;
    }
}
