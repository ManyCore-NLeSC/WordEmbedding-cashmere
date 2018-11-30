package nl.esciencecenter.wordembedding.math;

import nl.esciencecenter.wordembedding.data.PMITable;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.data.WordEmbedding;

public class Mean
{
    public static float compute(float [][] vector)
    {
        long nrElements = 0;
        float mean = 0;
        for ( int itemOne = 0; itemOne < vector.length; itemOne++ )
        {
            for ( int itemTwo = itemOne; itemTwo < vector[itemOne].length; itemTwo++ )
            {
                if ( Float.isFinite(vector[itemOne][itemTwo]) )
                {
                    nrElements++;
                    if ( nrElements == 1 )
                    {
                        mean = vector[itemOne][itemTwo];
                    }
                    else
                    {
                        float oldMean = mean;
                        mean = oldMean + ((vector[itemOne][itemTwo] - oldMean) / nrElements);
                    }
                }
            }
        }
        return mean;
    }

    public static float compute(Vocabulary vocabulary, PMITable table, boolean ppmi)
    {
        long nrElements = 0;
        float mean = 0;
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
                    nrElements++;
                    if ( nrElements == 1 )
                    {
                        mean = table.getPPMI(wordOne.getWord(), wordTwo.getWord());
                    }
                    else
                    {
                        float oldMean = mean;
                        mean = oldMean + ((table.getPPMI(wordOne.getWord(), wordTwo.getWord()) - oldMean) / nrElements);
                    }
                }
                else
                {
                    if ( Float.isFinite(table.getPMI(wordOne.getWord(), wordTwo.getWord())) )
                    {
                        nrElements++;
                        if ( nrElements == 1 )
                        {
                            mean = table.getPMI(wordOne.getWord(), wordTwo.getWord());
                        }
                        else
                        {
                            float oldMean = mean;
                            mean = oldMean + ((table.getPMI(wordOne.getWord(), wordTwo.getWord()) - oldMean) / nrElements);
                        }
                    }
                }
            }
        }
        return mean;
    }

    public static float compute(Vocabulary vocabulary, WordEmbedding words, WordEmbedding contexts)
    {
        long nrElements = 0;
        float mean = 0;
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
                if ( Float.isFinite(DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord()))) )
                {
                    nrElements++;
                    if ( nrElements == 1 )
                    {
                        mean = DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord()));
                    }
                    else
                    {
                        float oldMean = mean;
                        mean = oldMean + ((DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord())) - oldMean) / nrElements);
                    }
                }
            }
        }
        return mean;
    }
}