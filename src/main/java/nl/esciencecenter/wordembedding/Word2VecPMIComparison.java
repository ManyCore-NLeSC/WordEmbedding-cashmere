package nl.esciencecenter.wordembedding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.beust.jcommander.JCommander;

import nl.esciencecenter.wordembedding.commandline.Word2VecPMIComparisonCommandLineArguments;
import nl.esciencecenter.wordembedding.data.PMITable;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.data.WordPairs;
import nl.esciencecenter.wordembedding.utilities.io.ReadVocabulary;
import nl.esciencecenter.wordembedding.utilities.LearnWordPairs;
import nl.esciencecenter.wordembedding.utilities.io.ReadWord2VecWordVectors;
import nl.esciencecenter.wordembedding.validation.ComputeObjectiveFunction;


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
        vocabulary.sort();
        System.out.println("The vocabulary contains " + (vocabulary.getNrWords() - 1) +  " words; the total number of occurrences is " + (vocabulary.getOccurrences() - vocabulary.getWord("</s>").getOccurrences()) + ".");
        // Read word and context vectors
        try {
            file = new BufferedReader(new FileReader(arguments.getVectorFileName()));
            words = ReadWord2VecWordVectors.read(file);
            file.close();
        } catch ( IOException err ) {
            System.err.println("Impossible to open \"" + arguments.getVectorFileName() + "\".");
            return;
        }
        System.out.println("Word2Vec vectors loaded.");
        try {
            file = new BufferedReader(new FileReader(arguments.getContextFileName()));
            contexts = ReadWord2VecWordVectors.read(file);
            file.close();
        } catch ( IOException err ) {
            System.err.println("Impossible to open \"" + arguments.getContextFileName() + "\".");
            return;
        }
        System.out.println("Word2Vec context vectors loaded.");
        // Learn all pairs and generate PMI table
        try {
            pairs = new WordPairs(arguments.getWindow());
            file = new BufferedReader(new FileReader(arguments.getCorpusFileName()));
            LearnWordPairs.learn(pairs, vocabulary, file);
            file.close();
        } catch ( IOException err ) {
            System.err.println("Impossible to open \"" + arguments.getCorpusFileName() + "\".");
            return;
        }
        System.out.println("The corpus contains " + pairs.getUniquePairs() + " word pairs; the total number of occurrences is " + pairs.getTotalPairs() + ".");
        pmiTable = new PMITable(vocabulary, pairs);
        // Empty line
        System.out.println();
        // Compute statistics and differences
        System.out.println("The value of the objective function for Word2Vec is: " + ComputeObjectiveFunction.objectiveFunctionWord2Vec(vocabulary, pairs, words, contexts, arguments.getNegativeSamples()));
    }

    private static Word2VecPMIComparisonCommandLineArguments parseCommandLine(String[] args)
    {
        Word2VecPMIComparisonCommandLineArguments arguments = new Word2VecPMIComparisonCommandLineArguments();
        JCommander commander = JCommander.newBuilder().addObject(arguments).build();
        commander.parse(args);
        if ( arguments.getHelp() )
        {
            commander.usage();
            return null;
        }
        return arguments;
    }
}