package nl.esciencecenter.wordembedding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.beust.jcommander.JCommander;

import nl.esciencecenter.wordembedding.commandline.Word2VecPMIComparisonCommandLineArguments;
import nl.esciencecenter.wordembedding.data.*;
import nl.esciencecenter.wordembedding.utilities.ComputeObjectiveFunction;
import nl.esciencecenter.wordembedding.utilities.ReduceVocabulary;
import nl.esciencecenter.wordembedding.utilities.io.ReadVocabulary;
import nl.esciencecenter.wordembedding.utilities.LearnWordPairs;
import nl.esciencecenter.wordembedding.utilities.io.ReadWord2VecWordVectors;
import nl.esciencecenter.wordembedding.validation.EvaluateMatrixSimilarity;


public class Word2VecPMIComparison
{
    public static void main(String [] args)
    {
        long maxPairs;
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
        System.out.println("Word2Vec word vectors loaded.");
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
        if ( arguments.getMaxPairs() > 0 )
        {
            maxPairs = arguments.getMaxPairs();
            pairs.sort();
        }
        else
        {
            maxPairs = pairs.getUniquePairs();
        }
        pmiTable = new PMITable(pairs);
        if ( arguments.getSamplingRate() > 0 )
        {
            ReduceVocabulary.reduce(vocabulary, arguments.getSamplingRate());
            vocabulary.sort();
            System.out.println("The reduced vocabulary contains " + (vocabulary.getNrWords() - 1) +  " words; the total number of occurrences is " + (vocabulary.getOccurrences() - vocabulary.getWord("</s>").getOccurrences()) + ".");
        }
        // Empty line
        System.out.println();
        // Compute statistics and differences
        if ( arguments.getDistance() )
        {
            System.out.println("Average distance between Word2Vec and PMI: " + EvaluateMatrixSimilarity.distanceFromPMIWord2Vec(pairs, pmiTable, words, contexts, arguments.getNegativeSamples(), maxPairs));
        }
        if ( arguments.getDeviation() )
        {
            ObjectiveFunction objective = new ObjectiveFunction();
            try {
                file = new BufferedReader(new FileReader(arguments.getCorpusFileName()));
                ComputeObjectiveFunction.compute(objective, vocabulary, pmiTable, words, contexts, arguments.getNegativeSamples(), file);
                file.close();
            } catch ( IOException err ) {
                System.err.println("Impossible to open \"" + arguments.getCorpusFileName() + "\".");
                return;
            }
            System.out.println("The deviation from the optimal objective value is " + String.format("%.2f", objective.getPercentageOfDeviation()) + "%");
        }
        if ( arguments.getFrobenius() )
        {
            System.out.println("The Frobenius norm of Word2Vec is: " + EvaluateMatrixSimilarity.computeFrobeniusNorm(pairs, words, contexts, maxPairs));
            System.out.println("The Frobenius norm of PMI is: " + EvaluateMatrixSimilarity.computeFrobeniusNorm(pairs, pmiTable, arguments.getNegativeSamples(), maxPairs));
            System.out.println("The Frobenius norm of (PMI - Word2Vec) is: " + EvaluateMatrixSimilarity.computeFrobeniusNorm(pairs, pmiTable, words, contexts, arguments.getNegativeSamples(), maxPairs));
        }
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