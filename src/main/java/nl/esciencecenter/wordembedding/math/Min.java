package nl.esciencecenter.wordembedding.math;

import nl.esciencecenter.wordembedding.data.PMITable;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.data.WordEmbedding;

public class Min
{
    public static float compute(float [][] vector)
    {
        float min = Float.MAX_VALUE;
        for ( int itemOne = 0; itemOne < vector.length; itemOne++ )
        {
            for ( int itemTwo = itemOne; itemTwo < vector[itemOne].length; itemTwo++ )
            {
                if ( vector[itemOne][itemTwo] < min )
                {
                    min = vector[itemOne][itemTwo];
                }
            }
        }
        return min;
    }

    public static float compute(Vocabulary vocabulary, PMITable table, boolean ppmi)
    {
        float min = Float.MAX_VALUE;
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
                if ( ppmi )
                {
                    if ( table.getPPMI(wordOne.getWord(), wordTwo.getWord()) < min )
                    {
                        min = table.getPPMI(wordOne.getWord(), wordTwo.getWord());
                    }
                }
                else
                {
                    if ( table.getPMI(wordOne.getWord(), wordTwo.getWord()) < min )
                    {
                        min = table.getPMI(wordOne.getWord(), wordTwo.getWord());
                    }
                }
            }
        }
        return min;
    }

    public static float compute(Vocabulary vocabulary, WordEmbedding words, WordEmbedding contexts)
    {
        float min = Float.MAX_VALUE;
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
                if ( DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord())) < min )
                {
                    min = DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord()));
                }
            }
        }
        return min;
    }
}