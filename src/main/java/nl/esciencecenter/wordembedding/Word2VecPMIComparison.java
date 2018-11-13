package nl.esciencecenter.wordembedding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
        float [] min = new float[3];
        float [] max = new float[3];
        float [] mean = new float[3];
        float [] standardDeviation = new float[3];
        long [] histogram;
        float spearmanCorrelation = 0;

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
        System.out.println();
        // Compute statistics
        if ( arguments.getMin() )
        {
            min[0] = Min.compute(vocabulary, words, contexts);
            min[1] = Min.compute(vocabulary, pmiTable, arguments.getPPMI());
            min[2] = Min.compute(differences);
            System.out.println("Minimum Word2Vec value: " + min[0]);
            System.out.println("Minimum PMI value: " + min[1]);
            System.out.println("Minimum difference: " + min[2]);
            System.out.println();
        }
        if ( arguments.getMean() )
        {
            mean[0] = Mean.compute(vocabulary, words, contexts);
            mean[1] = Mean.compute(vocabulary, pmiTable, arguments.getPPMI());
            mean[2] = Mean.compute(differences);
            System.out.println("Mean Word2Vec value: " + mean[0]);
            System.out.println("Mean PMI value: " + mean[1]);
            System.out.println("Mean difference: " + mean[2]);
            System.out.println();
        }
        if ( arguments.getStandardDeviation() )
        {
            standardDeviation[0] = StandardDeviation.compute(vocabulary, words, contexts);
            standardDeviation[1] = StandardDeviation.compute(vocabulary, pmiTable, arguments.getPPMI());
            standardDeviation[2] = StandardDeviation.compute(differences);
            System.out.println("Standard deviation Word2Vec value: " + standardDeviation[0]);
            System.out.println("Standard deviation PMI value: " + standardDeviation[1]);
            System.out.println("Standard deviation of differences: " + standardDeviation[2]);
            System.out.println();
        }
        if ( arguments.getMax() )
        {
            max[0] = Max.compute(vocabulary, words, contexts);
            max[1] = Max.compute(vocabulary, pmiTable, arguments.getPPMI());
            max[2] = Max.compute(differences);
            System.out.println("Maximum Word2Vec value: " + max[0]);
            System.out.println("Maximum PMI value: " + max[1]);
            System.out.println("Maximum difference: " + max[2]);
            System.out.println();
        }
        if ( arguments.getHistogram() && (arguments.getMin() && arguments.getMax()) )
        {
            histogram = Histogram.compute(differences, min[2], max[2]);
            System.out.println("\nHistogram of differences\n");
            Histogram.print(histogram, min[2], max[2]);
            System.out.println();
        }
        if ( arguments.getSpearman() )
        {
            ArrayList<float []> couples = new ArrayList<>();
            for ( Word wordOne : vocabulary.getWords() )
            {
                if ( wordOne.getWord().equals("</s>") )
                {
                    continue;
                }
                for ( Word wordTwo : vocabulary.getWords() )
                {
                    if ( wordTwo.getWord().equals("</s>") )
                    {
                        continue;
                    }
                    float [] couple = new float [2];
                    if ( arguments.getPPMI() )
                    {
                        couple[0] = DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord()));
                        couple[1] = pmiTable.getPPMI(wordOne.getWord(), wordTwo.getWord());
                    }
                    else
                    {
                        couple[0] = DotProduct.compute(words.getWordCoordinates(wordOne.getWord()), contexts.getWordCoordinates(wordTwo.getWord()));
                        couple[1] = pmiTable.getPMI(wordOne.getWord(), wordTwo.getWord());
                    }
                    couples.add(couple);
                }
            }
            spearmanCorrelation = SpearmanRankCorrelation.compute(couples);
            System.out.println("Spearman correlation: " + spearmanCorrelation);
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