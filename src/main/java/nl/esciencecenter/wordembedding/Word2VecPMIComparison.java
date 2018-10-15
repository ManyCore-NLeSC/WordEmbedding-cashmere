package nl.esciencecenter.wordembedding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import nl.esciencecenter.wordembedding.data.PMITable;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.data.WordPairs;
import nl.esciencecenter.wordembedding.math.DotProduct;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.utilities.io.ReadVocabulary;
import nl.esciencecenter.wordembedding.utilities.LearnWordPairs;
import nl.esciencecenter.wordembedding.utilities.io.ReadWord2VecWordVectors;


public class Word2VecPMIComparison
{
    public static void main(String [] args)
    {
        BufferedReader file;
        Vocabulary vocabulary;
        WordPairs pairs;
        PMITable pmiTable;
        WordEmbedding words, contexts;
        float [] differences;

        if ( args.length != 5 ) {
            System.err.println("Usage: Word2VecPMIComparison <vocabulary_file> <window_size> <corpus_file> <word_vectors> <context_vectors>");
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
        pmiTable = new PMITable(vocabulary, pairs);
        // Read word and context vectors
        try {
            file = new BufferedReader(new FileReader(args[3]));
            words = ReadWord2VecWordVectors.read(file);
            file.close();
        } catch ( IOException err ) {
            System.err.println("Impossible to open \"" + args[3] + "\".");
            return;
        }
        try {
            file = new BufferedReader(new FileReader(args[4]));
            contexts = ReadWord2VecWordVectors.read(file);
            file.close();
        } catch ( IOException err ) {
            System.err.println("Impossible to open \"" + args[4] + "\".");
            return;
        }
        differences = new float [(vocabulary.getNrWords() - 1) * (vocabulary.getNrWords() - 1)];
        int wordOneIndex = 0;
        for ( Word wordOne : vocabulary.getWords() )
        {
            int wordTwoIndex = 0;
            for ( Word wordTwo : vocabulary.getWords() )
            {
                differences[(wordOneIndex * (vocabulary.getNrWords() - 1)) + wordTwoIndex] = DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), words.getWordCoordinates(wordTwo.getWord())) - pmiTable.getPMI(wordOne.getWord(), wordTwo.getWord());
                wordTwoIndex++;
            }
            wordOneIndex++;
        }
    }
}