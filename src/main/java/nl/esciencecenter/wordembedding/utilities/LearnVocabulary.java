package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.Vocabulary;

import java.io.BufferedReader;
import java.io.IOException;

public class LearnVocabulary extends Thread {
    private static double fillingThreshold = 0.7;
    private final Vocabulary vocabulary;
    private final BufferedReader fileReader;
    private final Boolean strict;


    public LearnVocabulary(Vocabulary vocabulary, BufferedReader fileReader, Boolean strict) {
        this.vocabulary = vocabulary;
        this.fileReader = fileReader;
        this.strict = strict;
    }

    public void run() {
        String line;

        try {
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
        } catch ( IOException err ) {
            return;
        }
    }
}
