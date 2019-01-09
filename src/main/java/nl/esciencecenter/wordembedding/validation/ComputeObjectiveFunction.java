package nl.esciencecenter.wordembedding.validation;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.data.WordPairs;
import nl.esciencecenter.wordembedding.math.DotProduct;
import nl.esciencecenter.wordembedding.math.Negate;
import nl.esciencecenter.wordembedding.math.Sigmoid;

public class ComputeObjectiveFunction {
    public static double compute(Vocabulary vocabulary, WordPairs pairs, WordEmbedding words, WordEmbedding contexts, int k)
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
}
