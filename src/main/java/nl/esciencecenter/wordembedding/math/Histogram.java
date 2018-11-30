package nl.esciencecenter.wordembedding.math;

import nl.esciencecenter.wordembedding.data.PMITable;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.data.WordEmbedding;

public class Histogram
{
    public static long [] compute(float [][] vector, int histogramSize, float min, float max)
    {
        long [] histogram = new long [histogramSize];
        for ( int itemOne = 0; itemOne < vector.length; itemOne++ )
        {
            for ( int itemTwo = itemOne; itemTwo < vector[itemOne].length; itemTwo++ ) {
                if ( Float.isFinite(vector[itemOne][itemTwo]) )
                {
                    histogram[(int)(((vector[itemOne][itemTwo] - min) * (histogram.length - 1)) / (max - min))]++;
                }
            }
        }
        return histogram;
    }

    public static long [] compute(Vocabulary vocabulary, PMITable table, int histogramSize, float min, float max, boolean ppmi)
    {
        long [] histogram = new long [histogramSize];
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
                    histogram[(int)(((table.getPPMI(wordOne.getWord(), wordTwo.getWord()) - min) * (histogram.length - 1)) / (max - min))]++;
                }
                else
                {
                    if ( Float.isFinite(table.getPMI(wordOne.getWord(), wordTwo.getWord())) )
                    {
                        histogram[(int)(((table.getPMI(wordOne.getWord(), wordTwo.getWord()) - min) * (histogram.length - 1)) / (max - min))]++;
                    }
                }
            }
        }
        return histogram;
    }

    public static long [] compute(Vocabulary vocabulary, WordEmbedding words, WordEmbedding contexts, int histogramSize, float min, float max)
    {
        long [] histogram = new long [histogramSize];
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
                histogram[(int)(((DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord())) - min) * (histogram.length - 1)) / (max - min))]++;
            }
        }
        return histogram;
    }

    public static long [] compute(Vocabulary vocabulary, WordEmbedding words, WordEmbedding contexts, PMITable table, int histogramSize, float min, float max)
    {
        long [] histogram = new long [histogramSize];
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
                    histogram[(int)(((DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord())) - min) * (histogram.length - 1)) / (max - min))]++;
                }
            }
        }
        return histogram;
    }

    public static void print(long [] histogram, float min, float max)
    {
        for ( int item = 0; item < histogram.length; item++ )
        {
            System.out.println((item) + " " + (min + (item * ((max - min) / histogram.length))) + " " + histogram[item]);
        }
    }
}
