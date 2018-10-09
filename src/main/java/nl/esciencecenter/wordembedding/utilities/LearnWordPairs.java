package nl.esciencecenter.wordembedding.utilities;

import java.io.BufferedReader;
import java.io.IOException;

import nl.esciencecenter.wordembedding.data.WordPairs;

public class LearnWordPairs
{
    public static void learn(WordPairs pairs, BufferedReader fileReader) throws IOException
    {
        String line;

        while ( (line = fileReader.readLine()) != null ) {
            String [] values = line.split("[ \t]+");
            for ( int word = 0; word < values.length; word++ )
            {
                for ( int targetWord = word - pairs.getWindowSize(); targetWord < word + pairs.getWindowSize(); targetWord++ )
                {
                    if ( (word != targetWord) && (targetWord >= 0) && (targetWord < values.length) )
                    {
                        pairs.addPair(values[word], values[targetWord]);
                    }
                }
            }
        }
    }
}