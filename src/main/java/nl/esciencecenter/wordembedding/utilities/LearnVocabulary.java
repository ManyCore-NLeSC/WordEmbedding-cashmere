package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.Vocabulary;

import java.io.BufferedReader;
import java.io.IOException;

public class LearnVocabulary {
    public static void learn(Vocabulary vocabulary, BufferedReader fileReader, Boolean strict) throws IOException {
        final Double fillingThreshold = 0.7;
        String line;

        while ( (line = fileReader.readLine()) != null ) {
            vocabulary.addWord("</s>");
            while ( !line.isEmpty() ) {
                String word = ReadWord.readWord(line, strict);

                if ( word == null ) {
                    line = line.trim();
                    continue;
                } else {
                    line = line.substring(word.length());
                    line = line.trim();
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