package nl.esciencecenter.wordembedding.utilities.io;

import nl.esciencecenter.wordembedding.data.WordSimilarity;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadWordSimilarity {
    public static void read(WordSimilarity similarities, BufferedReader fileReader) throws IOException {
        String line;

        while ( (line = fileReader.readLine()) != null ) {
            String [] values = line.split("[ \t]+");
            similarities.addSimilarityScore(values[0], values[1], Float.parseFloat(values[2]));
        }
    }
}
