package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;

import java.util.ArrayList;
import java.util.Random;

public class ReduceVocabulary {
    public static void reduce(Vocabulary vocabulary) {
        ArrayList<String> wordsToRemove = new ArrayList<>();

        for ( Word word : vocabulary.getWords() ) {
            if ( word.getOccurrences() < vocabulary.getOccurrenceThreshold() ) {
                wordsToRemove.add(word.getWord());
            }
        }
        for ( String word : wordsToRemove ) {
            vocabulary.removeWord(word);
        }
        vocabulary.incrementOccurrenceThreshold(1);
    }

    public static void reduce(Vocabulary vocabulary, int probability) {
        Random generator = new Random();
        for ( Word word : vocabulary.getWords() ) {
            if ( word.getWord().equals("</s>") )
            {
                continue;
            }
            if ( generator.nextInt(100) < probability ) {
                vocabulary.removeWord(word.getWord());
            }
        }
    }
}
