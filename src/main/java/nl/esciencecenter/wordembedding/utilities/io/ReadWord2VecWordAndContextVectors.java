package nl.esciencecenter.wordembedding.utilities.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;

public class ReadWord2VecWordAndContextVectors {
    public static void read(String fileName, int vectorSize, float[] wordVectors, float[] contextVectors) throws IOException
    {
        if (fileName.contains(".txt"))
        {
            // Text file
            BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
            // Skip the first line
            fileReader.readLine();
            // Read the word vectors
            ReadWord2VecVectors.readTextVectors(vectorSize, wordVectors, fileReader);
            // Skip the first line
            fileReader.readLine();
            // Read the context vectors
            ReadWord2VecVectors.readTextVectors(vectorSize, contextVectors, fileReader);
            fileReader.close();
        }
        else
        {
            // Binary file
            RandomAccessFile file = new RandomAccessFile(fileName, "r");
            FileChannel fileReader = file.getChannel();
            FloatBuffer buffer = fileReader.map(FileChannel.MapMode.READ_ONLY, 0, fileReader.size()).order(ByteOrder.nativeOrder()).asFloatBuffer();
            for (int element = 0; element < vectorSize; element++)
            {
                wordVectors[element] = buffer.get();
            }
            for (int element = 0; element < vectorSize; element++)
            {
                contextVectors[element] = buffer.get();
            }
            fileReader.close();
            file.close();
        }
    }
}
