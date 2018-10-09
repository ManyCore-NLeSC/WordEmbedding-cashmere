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
        return read(fileName, (long)(0));
    }

    public static float [] read(String fileName, long bytesToSkip) throws IOException
    {
        float [] vectors;

        int nrWords;
        int dimensions;
        String line;
        String [] values;
        BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
        fileReader.skip(bytesToSkip);
        // Read the first line
        line = fileReader.readLine();
        values = line.split("[ \t]+");
        nrWords = Integer.parseInt(values[0]);
        dimensions = Integer.parseInt(values[1]);
        vectors = new float [nrWords * dimensions];
        // Read the vectors
        readTextVectors(dimensions, vectors, fileReader);
        fileReader.close();
        return vectors;
    }

    public static float [] read(String fileName, int nrElements) throws IOException
    {
        return read(fileName, nrElements, 0);
    }

    public static float [] read(String fileName, int nrElements, long bytesToSkip) throws IOException
    {
        float [] vectors = new float [nrElements];

        RandomAccessFile file = new RandomAccessFile(fileName, "r");
        FileChannel fileReader = file.getChannel();
        fileReader.position(bytesToSkip);
        FloatBuffer buffer = fileReader.map(FileChannel.MapMode.READ_ONLY, 0, fileReader.size()).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for (int element = 0; element < nrElements; element++)
        {
            vectors[element] = buffer.get();
        }
        fileReader.close();
        file.close();
        return vectors;
    }

    static void readTextVectors(int vectorSize, float[] vectors, BufferedReader fileReader) throws IOException
    {
        int wordIndex = 0;
        String line;
        String[] values;
        while ((line = fileReader.readLine()) != null)
        {
            values = line.split("[ \t]+");
            for (int dimension = 0; dimension < vectorSize; dimension++)
            {
                vectors[(wordIndex * vectorSize) + dimension] = Float.parseFloat(values[dimension + 1]);
            }
            wordIndex++;
        }
    }
}
