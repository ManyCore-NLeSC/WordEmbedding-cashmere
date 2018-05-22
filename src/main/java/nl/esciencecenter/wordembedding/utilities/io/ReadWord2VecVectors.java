package nl.esciencecenter.wordembedding.utilities.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadWord2VecVectors {
    public static float [] read(String fileName) throws IOException
    {
        int nrWords;
        int dimensions;
        String line;
        String [] values;
        float [] vectors;

        BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
        // Read the first line
        line = fileReader.readLine();
        values = line.split(" ");
        nrWords = Integer.parseInt(values[0]);
        dimensions = Integer.parseInt(values[1]);
        vectors = new float [nrWords * dimensions];
        // Read the vectors
        int wordIndex = 0;
        while ( (line = fileReader.readLine()) != null ) {
            values = line.split(" ");
            for ( int dimension = 0; dimension < dimensions; dimension++ ) {
                vectors[(wordIndex * dimensions) + dimension] = Float.parseFloat(values[dimension + 1]);
            }
            wordIndex++;
        }
        return vectors;
    }
}
