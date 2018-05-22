package nl.esciencecenter.wordembedding.utilities.io;

import java.io.IOException;

public class ReadWord2VecContextVectors {
    public static float [] read(String fileName) throws IOException
    {
        return ReadWord2VecVectors.read(fileName);
    }
}
