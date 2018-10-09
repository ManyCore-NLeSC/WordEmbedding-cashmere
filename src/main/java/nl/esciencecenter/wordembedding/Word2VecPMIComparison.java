package nl.esciencecenter.wordembedding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.WordPairs;
import nl.esciencecenter.wordembedding.utilities.io.ReadVocabulary;
import nl.esciencecenter.wordembedding.utilities.LearnWordPairs;


public class Word2VecPMIComparison
{
    public static void main(String [] args)
    {
        BufferedReader file;
        Vocabulary vocabulary;
        WordPairs pairs;

        if ( args.length != 3 ) {
            System.err.println("Usage: Word2VecPMIComparison <vocabulary_file> <window_size> <corpus_file>");
            return;
        }
        // Read the vocabulary
        try {
            vocabulary = new Vocabulary();
            file = new BufferedReader(new FileReader(args[0]));
            ReadVocabulary.read(vocabulary, file);
            file.close();
        } catch ( IOException err ) {
            System.err.println("Impossible to open \"" + args[0] + "\".");
            return;
        }
        // Learn all pairs
        try {
            pairs = new WordPairs();
            pairs.setWindowSize(Integer.parseInt(args[1]));
            file = new BufferedReader(new FileReader(args[2]));
            LearnWordPairs.learn(pairs, file);
            file.close();
        } catch ( IOException err ) {
            System.err.println("Impossible to open \"" + args[2] + "\".");
            return;
        }
    }
}