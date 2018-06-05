package nl.esciencecenter.wordembedding.utilities.io;

import nl.esciencecenter.wordembedding.data.WordEmbedding;

import java.io.BufferedReader;
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
        values = line.split("[ \t]+");
        dimensions = Integer.parseInt(values[1]);
        embedding = new WordEmbedding(dimensions);
        // Read the vectors
        while ( (line = fileReader.readLine()) != null ) {
            float [] coordinates = new float [dimensions];
            values = line.split("[ \t]+");
            for ( int dimension = 0; dimension < dimensions; dimension++ ) {
                coordinates[dimension] = Float.parseFloat(values[dimension + 1]);
            }
            embedding.addWord(values[0], coordinates);
        }

        return embedding;
    }

    public static float [] read(String fileName) throws IOException
    {
       return ReadWord2VecVectors.read(fileName);
    }

    public static float [] read(String fileName, int nrElements) throws IOException
    {
        return ReadWord2VecVectors.read(fileName, nrElements);
    }
}
