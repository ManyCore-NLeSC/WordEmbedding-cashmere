package nl.esciencecenter.wordembedding.math;

import nl.esciencecenter.wordembedding.data.PMITable;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.data.WordEmbedding;

public class Max
{
    public static float compute(float [][] vector)
    {
        float max = Float.MIN_VALUE;
        for ( int itemOne = 0; itemOne < vector.length; itemOne++ )
        {
            for ( int itemTwo = itemOne; itemTwo < vector[itemOne].length; itemTwo++ )
            {
                if ( Float.isFinite(vector[itemOne][itemTwo]) && vector[itemOne][itemTwo] > max )
                {
                    max = vector[itemOne][itemTwo];
                }
            }
        }
        return max;
    }

    public static float compute(Vocabulary vocabulary, PMITable table, boolean ppmi)
    {
        float max = Float.MIN_VALUE;
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
                    if ( table.getPPMI(wordOne.getWord(), wordTwo.getWord()) > max )
                    {
                        max = table.getPPMI(wordOne.getWord(), wordTwo.getWord());
                    }
                }
                else
                {
                    if ( Float.isFinite(table.getPMI(wordOne.getWord(), wordTwo.getWord())) && table.getPMI(wordOne.getWord(), wordTwo.getWord()) > max )
                    {
                        max = table.getPMI(wordOne.getWord(), wordTwo.getWord());
                    }
                }
            }
        }
        return max;
    }

    public static float compute(Vocabulary vocabulary, WordEmbedding words, WordEmbedding contexts)
    {
        float max = Float.MIN_VALUE;
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
                if ( DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord())) > max )
                {
                    max = DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord()));
                }
            }
        }
        return max;
    }

    public static float compute(Vocabulary vocabulary, WordEmbedding words, WordEmbedding contexts, PMITable table)
    {
        float max = Float.MIN_VALUE;
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
                if ( Float.isFinite(table.getPMI(wordOne.getWord(), wordTwo.getWord())) && DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord())) > max )
                {
                    max = DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord()));
                }
            }
        }
        return max;
    }
}