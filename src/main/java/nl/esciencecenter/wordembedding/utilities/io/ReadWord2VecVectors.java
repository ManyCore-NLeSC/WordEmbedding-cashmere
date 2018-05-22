package nl.esciencecenter.wordembedding.utilities.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;

public class ReadWord2VecVectors {
    public static float [] read(String fileName) throws IOException
    {
        float [] vectors;

        if (fileName.contains(".txt"))
        {
            int nrWords;
            int dimensions;
            String line;
            String [] values;
            BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
            // Read the first line
            line = fileReader.readLine();
            values = line.split("[ \t]");
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
            fileReader.close();
        }
        else
        {
            FileChannel fileReader = new RandomAccessFile(fileName, "r").getChannel();
            int nrElements = (int)(fileReader.size() / 4);
            vectors = new float [nrElements];
            FloatBuffer buffer = fileReader.map(FileChannel.MapMode.READ_ONLY, 0, fileReader.size()).order(ByteOrder.nativeOrder()).asFloatBuffer();
            for (int element = 0; element < nrElements; element++)
            {
                vectors[element] = buffer.get(element);
            }
            fileReader.close();
        }
        return vectors;
    }
}
