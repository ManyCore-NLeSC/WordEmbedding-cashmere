package nl.esciencecenter.word2vec.utilities;

import nl.esciencecenter.word2vec.data.Vocabulary;
import nl.esciencecenter.word2vec.data.Word;

import java.io.BufferedReader;
import java.io.IOException;

public class LearnVocabulary {
    private final Double fillingThreshold = 0.7;

    public void learn(Vocabulary vocabulary, BufferedReader fileReader, Boolean strict) throws IOException {
        String line;

        while ( (line = fileReader.readLine()) != null ) {
            vocabulary.addWord("</s>");
            for ( String word : line.split("[ \t]") ) {
                if ( strict ) {
                    word = word.replaceAll("\\W", "");
                }
                vocabulary.addWord(word);
                if ( vocabulary.getNrWords() > vocabulary.getMaxSize() * fillingThreshold ) {
                    vocabulary.reduce();
                }
            }
        }
        vocabulary.reduce();
    }

}
