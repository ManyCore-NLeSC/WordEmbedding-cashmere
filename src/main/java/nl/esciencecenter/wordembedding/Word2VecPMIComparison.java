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
import nl.esciencecenter.wordembedding.math.*;
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
        // Statistics
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        float mean = 0;
        float standardDeviation = 0;
        long [] histogram;

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
        System.out.println("The corpus contains " + pairs.getTotalPairs() + " word pairs.");
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
                if ( arguments.getPPMI() )
                {
                    differences[wordOneIndex][wordTwoIndex] = DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord())) - pmiTable.getPPMI(wordOne.getWord(), wordTwo.getWord());
                }
                else
                {
                    differences[wordOneIndex][wordTwoIndex] = DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord())) - pmiTable.getPMI(wordOne.getWord(), wordTwo.getWord());
                }
                wordTwoIndex++;
            }
            wordOneIndex++;
        }
        // Compute statistics
        if ( arguments.getMin() )
        {
            min = Min.compute(differences);
            System.out.println("Minimum difference: " + min);
        }
        if ( arguments.getMean() )
        {
            mean = Mean.compute(differences);
            System.out.println("Mean difference: " + mean);
        }
        if ( arguments.getStandardDeviation() )
        {
            standardDeviation = StandardDeviation.compute(differences);
            System.out.println("Standard deviation of differences: " + standardDeviation);
        }
        if ( arguments.getMax() )
        {
            max = Max.compute(differences);
            System.out.println("Maximum difference: " + max);
        }
        if ( arguments.getHistogram() && (arguments.getMin() && arguments.getMax()) )
        {
            histogram = Histogram.compute(differences, min, max);
            System.out.println("\nHistogram of differences\n");
            Histogram.print(histogram, min, max);
            System.out.println();
        }
    }

    private static Word2VecPMIComparisonCommandLineArguments parseCommandLine(String[] args) {
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