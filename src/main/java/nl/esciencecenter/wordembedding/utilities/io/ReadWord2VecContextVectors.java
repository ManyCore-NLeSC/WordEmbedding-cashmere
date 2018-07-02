package nl.esciencecenter.wordembedding.utilities.io;

import java.io.IOException;

public class ReadWord2VecContextVectors {
    public static float [] read(String fileName) throws IOException
    {
        return ReadWord2VecVectors.read(fileName);
    }

    public static float [] read(String fileName, long bytesToSkip) throws IOException
    {
        return ReadWord2VecVectors.read(fileName, bytesToSkip);
    }

    public static float [] read(String fileName, int nrElements) throws IOException
    {
        return ReadWord2VecVectors.read(fileName, nrElements);
    }

    public static float [] read(String fileName, int nrElements, long bytesToSkip) throws IOException
    {
        return ReadWord2VecVectors.read(fileName, nrElements, bytesToSkip);
    }
}
