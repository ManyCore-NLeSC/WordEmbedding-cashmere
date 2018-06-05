package nl.esciencecenter.wordembedding.utilities.io;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadVocabulary {
    public static void read(Vocabulary vocabulary, BufferedReader fileReader) throws IOException {
        String line;

        while ( (line = fileReader.readLine()) != null ) {
            String [] values = line.split(" ");
            vocabulary.addWord(new Word(values[0], Integer.parseInt(values[1])));
        }
        if (vocabulary.getWord("</s>") == null)
        {
            vocabulary.addWord("</s>");
        }
    }
}
