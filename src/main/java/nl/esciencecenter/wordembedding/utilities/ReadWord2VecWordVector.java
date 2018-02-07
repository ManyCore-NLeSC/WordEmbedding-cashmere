package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.data.WordEmbedding;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadWord2VecWordVector {
    public static WordEmbedding read(BufferedReader fileReader) throws IOException {
        return read(null, fileReader);
    }

    public static WordEmbedding read(Vocabulary vocabulary, BufferedReader fileReader) throws IOException {
        Integer dimensions;
        String line;
        String [] values;
        Word word;
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
            if ( vocabulary == null ) {
                word = new Word(values[0]);
            } else {
                word = vocabulary.getWord(values[0]);
            }
            for ( int dimension = 0; dimension < dimensions; dimension++ ) {
                coordinates[dimension] = Float.parseFloat(values[dimension + 1]);
            }
            embedding.addWord(word, coordinates);
        }

        return embedding;
    }
}
