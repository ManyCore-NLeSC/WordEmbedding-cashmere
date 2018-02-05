package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;

import java.util.ArrayList;

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
}
