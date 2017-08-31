package nl.esciencecenter.word2vec.utilities;

import nl.esciencecenter.word2vec.data.Vocabulary;
import nl.esciencecenter.word2vec.data.Word;

import java.io.BufferedReader;
import java.io.IOException;

public class LearnVocabulary {

    public void learn(Vocabulary vocabulary, BufferedReader fileReader) throws IOException {
        String line;

        vocabulary.addWord(new Word("</s>"));
        while ( (line = fileReader.readLine()) != null ) {
            for ( String word : line.split("[ \t]") ) {
                vocabulary.addWord(new Word(word));
            }
        }
    }

}
