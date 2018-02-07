package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.WordEmbedding;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadWord2VecWordVector {
    public static WordEmbedding read(BufferedReader fileReader) throws IOException {
        Integer dimensions;
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
            Float [] coordinates = new Float [dimensions];
            values = line.split(" ");
            for ( int dimension = 0; dimension < dimensions; dimension++ ) {
                coordinates[dimension] = Float.parseFloat(values[dimension + 1]);
            }
            embedding.addWord(values[0], coordinates);
        }

        return embedding;
    }
}
