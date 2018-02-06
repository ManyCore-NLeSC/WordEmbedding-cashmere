package nl.esciencecenter.wordembedding.validation;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;

public class CompareVocabularies {
    public static Boolean compare(Vocabulary [] vocabularies) {
        if ( vocabularies.length > 1 ) {
            // First check size
            for ( int vocabularyID = 1; vocabularyID < vocabularies.length; vocabularyID++ ) {
                if ( !vocabularies[vocabularyID].getNrWords().equals(vocabularies[0].getNrWords()) ) {
                    return false;
                }
            }
            // If all vocabularies have the same size, check that they all contain the same words
            for ( int vocabularyID = 1; vocabularyID < vocabularies.length; vocabularyID++ ) {
                for ( Word word : vocabularies[vocabularyID].getWords() ) {
                    Word referenceWord = vocabularies[0].getWord(word.getWord());
                    if ( referenceWord == null ) {
                        return false;
                    }
                    if ( !word.getOccurrences().equals(referenceWord.getOccurrences()) ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
