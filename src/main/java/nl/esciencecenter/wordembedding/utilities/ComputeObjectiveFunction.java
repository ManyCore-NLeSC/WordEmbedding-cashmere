package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.ObjectiveFunction;
import nl.esciencecenter.wordembedding.data.PMITable;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.WordEmbedding;

import java.io.BufferedReader;
import java.io.IOException;

public class ComputeObjectiveFunction {
    public static void compute(ObjectiveFunction function, Vocabulary vocabulary, PMITable pmiTable, WordEmbedding words, WordEmbedding contexts, int k, boolean sampling, float samplingFraction, BufferedReader fileReader) throws IOException
    {
        String line;

        while ( (line = fileReader.readLine()) != null ) {
            String [] values = line.split("[ \t]+");
            for ( int word = 0; word < values.length; word++ )
            {
                for ( int targetWord = word - pmiTable.getPairs().getWindowSize(); targetWord <= word + pmiTable.getPairs().getWindowSize(); targetWord++ )
                {
                    if ( (word != targetWord) && (targetWord >= 0) && (targetWord < values.length) )
                    {
                        if ( (vocabulary.getWord(values[word]) != null) && (vocabulary.getWord(values[targetWord]) != null) )
                        {
                            if ( sampling )
                            {
                                String negativeSample = vocabulary.getRandomWord(samplingFraction);
                                function.incrementPMI(pmiTable, values[word], values[targetWord], negativeSample, k);
                                function.incrementWord2Vec(words, contexts, values[word], values[targetWord], negativeSample, k);
                            }
                            else
                            {
                                function.incrementPMI(pmiTable, values[word], values[targetWord], k);
                                function.incrementWord2Vec(words, contexts, values[word], values[targetWord], k);
                            }
                        }
                    }
                }
            }
        }
    }
}
