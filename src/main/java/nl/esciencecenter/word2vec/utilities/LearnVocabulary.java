package nl.esciencecenter.word2vec.utilities;

import nl.esciencecenter.word2vec.data.Vocabulary;

import java.io.BufferedReader;
import java.io.IOException;

public class LearnVocabulary {
    public static void learn(Vocabulary vocabulary, BufferedReader fileReader, Boolean strict) throws IOException {
        final Double fillingThreshold = 0.7;
        String line;

        while ( (line = fileReader.readLine()) != null ) {
            vocabulary.addWord("</s>");
            for ( String word : line.split("[ \t]") ) {
                if ( strict ) {
                    word = word.replaceAll("\\W", "");
                }
                if ( word.equals("") ) {
                    continue;
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
