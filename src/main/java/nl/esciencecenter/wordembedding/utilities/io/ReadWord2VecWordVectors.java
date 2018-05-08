package nl.esciencecenter.wordembedding.utilities.io;

import nl.esciencecenter.wordembedding.data.WordEmbedding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadWord2VecWordVectors {
    public static WordEmbedding read(BufferedReader fileReader) throws IOException
    {
        int dimensions;
        String line;
        String [] values;
        WordEmbedding embedding;

        // Read the first line
        line = fileReader.readLine();
        values = line.split(" ");
        dimensions = Integer.parseInt(values[1]);
        embedding = new WordEmbedding(dimensions);
        // Read the vectors
        while ( (line = fileReader.readLine()) != null ) {
            float [] coordinates = new float [dimensions];
            values = line.split(" ");
            for ( int dimension = 0; dimension < dimensions; dimension++ ) {
                coordinates[dimension] = Float.parseFloat(values[dimension + 1]);
            }
            embedding.addWord(values[0], coordinates);
        }

        return embedding;
    }

    public static float [] read(String fileName) throws IOException
    {
        int nrWords;
        int dimensions;
        String line;
        String [] values;
        float [] wordVectors;

        BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
        // Read the first line
        line = fileReader.readLine();
        values = line.split(" ");
        nrWords = Integer.parseInt(values[0]);
        dimensions = Integer.parseInt(values[1]);
        wordVectors = new float [nrWords * dimensions];
        // Read the vectors
        int wordIndex = 0;
        while ( (line = fileReader.readLine()) != null ) {
            values = line.split(" ");
            for ( int dimension = 0; dimension < dimensions; dimension++ ) {
                wordVectors[(wordIndex * dimensions) + dimension] = Float.parseFloat(values[dimension + 1]);
            }
            wordIndex++;
        }
        return wordVectors;
    }
}
