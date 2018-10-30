package nl.esciencecenter.wordembedding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.beust.jcommander.JCommander;

import org.apache.commons.math3.linear.Array2DRowFieldMatrix;

import nl.esciencecenter.wordembedding.commandline.Word2VecPMIComparisonCommandLineArguments;
import nl.esciencecenter.wordembedding.data.PMITable;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.data.WordPairs;
import nl.esciencecenter.wordembedding.math.DotProduct;
import nl.esciencecenter.wordembedding.math.Max;
import nl.esciencecenter.wordembedding.math.Mean;
import nl.esciencecenter.wordembedding.math.Min;
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
        Word2VecPMIComparisonCommandLineArguments arguments;
        float [][] differences;

        // Command line arguments parsing
        arguments = Word2VecPMIComparison.parseCommandLine(args);
        if ( arguments == null ) {
            System.err.println("Impossible to parse command line.");
            return;
        }
        // Read the vocabulary
        try {
            vocabulary = new Vocabulary();
            file = new BufferedReader(new FileReader(arguments.getVocabularyFileName()));
            ReadVocabulary.read(vocabulary, file);
            file.close();
        } catch ( IOException err ) {
            System.err.println("Impossible to open \"" + arguments.getVocabularyFileName() + "\".");
            return;
        }
        // Learn all pairs
        try {
            pairs = new WordPairs();
            pairs.setWindowSize(arguments.getWindow());
            file = new BufferedReader(new FileReader(arguments.getCorpusFileName()));
            LearnWordPairs.learn(pairs, vocabulary, file);
            file.close();
        } catch ( IOException err ) {
            System.err.println("Impossible to open \"" + arguments.getCorpusFileName() + "\".");
            return;
        }
        pmiTable = new PMITable(vocabulary, pairs);
        // Read word and context vectors
        try {
            file = new BufferedReader(new FileReader(arguments.getVectorFileName()));
            words = ReadWord2VecWordVectors.read(file);
            file.close();
        } catch ( IOException err ) {
            System.err.println("Impossible to open \"" + arguments.getVectorFileName() + "\".");
            return;
        }
        try {
            file = new BufferedReader(new FileReader(arguments.getContextFileName()));
            contexts = ReadWord2VecWordVectors.read(file);
            file.close();
        } catch ( IOException err ) {
            System.err.println("Impossible to open \"" + arguments.getContextFileName() + "\".");
            return;
        }
        differences = new float [vocabulary.getNrWords() - 1][vocabulary.getNrWords() - 1];
        int wordOneIndex = 0;
        for ( Word wordOne : vocabulary.getWords() )
        {
            if ( wordOne.getWord().equals("</s>") )
            {
                continue;
            }
            int wordTwoIndex = 0;
            for ( Word wordTwo : vocabulary.getWords() )
            {
                if ( wordTwo.getWord().equals("</s>") )
                {
                    continue;
                }
                differences[wordOneIndex][wordTwoIndex] = DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord())) - pmiTable.getPMI(wordOne.getWord(), wordTwo.getWord());
                wordTwoIndex++;
            }
            wordOneIndex++;
        }
        // Compute statistics
        if ( arguments.getMin() )
        {
            System.out.println("Minimum difference: " + Min.compute(differences));
        }
        // if ( arguments.getMean() )
        // {
        //     System.out.println("Mean difference: " + Mean.compute(differences));
        // }
        if ( arguments.getMax() )
        {
            System.out.println("Maximum difference: " + Max.compute(differences));
        }
    }

    static Word2VecPMIComparisonCommandLineArguments parseCommandLine(String [] args) {
        Word2VecPMIComparisonCommandLineArguments arguments = new Word2VecPMIComparisonCommandLineArguments();
        JCommander commander = JCommander.newBuilder().addObject(arguments).build();
        commander.parse(args);
        if ( arguments.getHelp() ) {
            commander.usage();
            return null;
        }
        return arguments;
    }
}