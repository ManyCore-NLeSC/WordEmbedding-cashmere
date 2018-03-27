package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.utilities.io.ReadWord;

import java.io.BufferedReader;
import java.io.IOException;

public class LearnVocabulary extends Thread {
    private final double fillingThreshold = 0.7;
    private final Vocabulary vocabulary;
    private final BufferedReader fileReader;
    private final boolean strict;

    public LearnVocabulary(Vocabulary vocabulary, BufferedReader fileReader, boolean strict) {
        this.vocabulary = vocabulary;
        this.fileReader = fileReader;
        this.strict = strict;
    }

    public void run() {
        String line;

        try {
            while ( (line = fileReader.readLine()) != null ) {
                synchronized ( vocabulary ) {
                    vocabulary.addWord("</s>");
                }
                while ( !line.isEmpty() ) {
                    String word = ReadWord.readWord(line, strict);

                    if ( word == null ) {
                        line = line.trim();
                        continue;
                    } else {
                        line = line.substring(word.length());
                        line = line.trim();
                    }
                    synchronized ( vocabulary ) {
                        vocabulary.addWord(word);
                        if ( vocabulary.getNrWords() > vocabulary.getMaxSize() * fillingThreshold ) {
                            ReduceVocabulary.reduce(vocabulary);
                        }
                    }
                }
            }
        } catch ( IOException err ) {
            err.printStackTrace();
        }
    }
}
