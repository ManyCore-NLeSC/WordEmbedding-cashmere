package nl.esciencecenter.word2vec.utilities;

import nl.esciencecenter.word2vec.data.Vocabulary;
import nl.esciencecenter.word2vec.data.Word;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadVocabulary {

    public void read(Vocabulary vocabulary, BufferedReader fileReader) throws IOException {
        String line;

        while ( (line = fileReader.readLine()) != null ) {
            String [] values = line.split(" ");
            vocabulary.addWord(new Word(values[0], Integer.parseInt(values[1])));
        }
    }
}
