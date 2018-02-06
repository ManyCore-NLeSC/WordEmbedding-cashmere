package nl.esciencecenter.wordembedding;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.utilities.ReadVocabulary;
import nl.esciencecenter.wordembedding.validation.CompareVocabularies;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Word2VecCompareVocabularies {
    public static void main(String [] args) {
        BufferedReader vocabularyFile;
        Vocabulary [] vocabularies;
        if ( args.length < 2 ) {
            System.err.println("Usage: " + args[0] + " <vocabulary_file> ... <vocabulary_file>");
            return;
        }
        // Load vocabularies from file
        vocabularies = new Vocabulary [args.length - 2];
        for ( int argument = 1; argument < args.length; argument++ ) {
            try {
                vocabularies[argument - 1] = new Vocabulary();
                vocabularyFile = new BufferedReader(new FileReader(args[argument]));
                ReadVocabulary.read(vocabularies[argument - 1], vocabularyFile);
                vocabularyFile.close();
            } catch ( IOException err ) {
                System.err.println("Impossible to open \"" + args[argument] + "\".");
                return;
            }
        }
        // Compare
        if ( CompareVocabularies.compare(vocabularies) ) {
            System.out.println("The vocabularies are equal.");
        } else  {
            System.out.println("The vocabularies are different.");
        }
    }
}
