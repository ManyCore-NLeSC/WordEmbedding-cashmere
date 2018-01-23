package nl.esciencecenter.wordembedding;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.network.Word2VecNeuralNetwork;
import nl.esciencecenter.wordembedding.utilities.LearnVocabulary;
import nl.esciencecenter.wordembedding.utilities.ReadVocabulary;
import nl.esciencecenter.wordembedding.utilities.SaveVocabulary;

import java.io.*;

public class Word2Vec {

    public static void main(String [] argv) {
        final Vocabulary vocabulary;

        // Command line arguments parsing
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander commander = JCommander.newBuilder().addObject(arguments).build();
        commander.parse(argv);
        if ( arguments.getHelp() ) {
            commander.usage();
            return;
        }
        // Read or learn vocabulary
        vocabulary = new Vocabulary(arguments.getMinCount());
        vocabulary.setMaxSize(arguments.getVocabularyMaxSize());
        if ( arguments.getInVocabularyFilename().length() > 0 ) {
            long timer = 0;
            BufferedReader inVocabularyFile;

            try {
                timer = System.nanoTime();
                inVocabularyFile = new BufferedReader(new FileReader(arguments.getInVocabularyFilename()));
                ReadVocabulary.read(vocabulary, inVocabularyFile);
                inVocabularyFile.close();
                timer = System.nanoTime() - timer;
            }catch ( IOException err) {
                err.printStackTrace();
            }
            if ( arguments.getDebug() ) {
                System.out.println("Read vocabulary from file \"" + arguments.getInVocabularyFilename() + "\".");
                System.out.println("Reading the vocabulary took " + (timer / 1.0e9) + " seconds.");
                System.out.println("The vocabulary contains " + vocabulary.getNrWords() + " words.");
            }
        } else {
            long timer = 0;
            final BufferedReader trainingFile;

            try {
                timer = System.nanoTime();
                trainingFile = new BufferedReader(new FileReader(arguments.getTrainingFilename()));
                for ( int thread = 0; thread < arguments.getNrThreads(); thread++ ) {
                    new LearnVocabulary(vocabulary, trainingFile, arguments.getStrict()).run();
                }
                trainingFile.close();
                vocabulary.reduce();
                timer = System.nanoTime() - timer;
            } catch ( IOException err ) {
                err.printStackTrace();
            }
            if ( arguments.getDebug() ) {
                System.out.println("Learned vocabulary from file \"" + arguments.getTrainingFilename() + "\".");
                System.out.println("Learning the vocabulary took " + (timer / 1.0e9) + " seconds.");
                System.out.println("The vocabulary contains " + vocabulary.getNrWords() + " words.");
            }
        }
        // Sort vocabulary
        vocabulary.sort();
        if ( arguments.getDebug() ) {
            System.out.println("The training file contains " + vocabulary.getOccurrences() + " useful words.");
            System.out.println();
        }
        // Initialize neural network
        Word2VecNeuralNetwork word2VecNeuralNetwork = new Word2VecNeuralNetwork(arguments.getUseCBOW(), arguments.getSoftmax(),
                arguments.getUsePosition(), arguments.getNegativeSamples(), arguments.getVectorDimensions(),
                arguments.getWindowSize(), arguments.getAlpha());
        word2VecNeuralNetwork.setDebug(arguments.getDebug());
        word2VecNeuralNetwork.initializeExponentialTable();
        word2VecNeuralNetwork.initialize(vocabulary);
        // Train neural network
        try {
            long timer;
            BufferedReader trainingFile;

            timer = System.nanoTime();
            trainingFile = new BufferedReader(new FileReader(arguments.getTrainingFilename()));
            word2VecNeuralNetwork.trainModel(vocabulary, trainingFile);
            trainingFile.close();
            timer = System.nanoTime() - timer;
            if ( arguments.getDebug() ) {
                System.out.println();
                System.out.println("Training the neural network took " + (timer / 1.0e9) + " seconds.");
                System.out.println("The neural network processed " + (vocabulary.getOccurrences() / (timer / 1.0e9))
                        + " words per second.");
            }
        } catch ( IOException err ) {
            err.printStackTrace();
        }
        // Save vocabulary
        if ( arguments.getOutVocabularyFilename().length() > 0 ) {
            long timer = 0;
            BufferedWriter outVocabularyFile;

            try {
                timer = System.nanoTime();
                outVocabularyFile = new BufferedWriter(new FileWriter(arguments.getOutVocabularyFilename()));
                SaveVocabulary.save(vocabulary, outVocabularyFile);
                outVocabularyFile.close();
                timer = System.nanoTime() - timer;
            } catch ( IOException err ) {
                err.printStackTrace();
            }
            if ( arguments.getDebug() ) {
                System.out.println("Saving the vocabulary took " + (timer / 1.0e9) + " seconds.");
            }
        }
        // Save learned vectors
        BufferedWriter outputFile;
        try {
            long timer = System.nanoTime();
            outputFile = new BufferedWriter(new FileWriter(arguments.getOutputFilename()));
            if ( arguments.getClasses() == 0 ) {
                word2VecNeuralNetwork.saveWordVectors(vocabulary, outputFile);
            } else {
                word2VecNeuralNetwork.saveClasses(vocabulary, outputFile, arguments.getClasses());
            }
            outputFile.close();
            timer = System.nanoTime() - timer;
            if ( arguments.getDebug() ) {
                System.out.println("Saving the output vectors took " + (timer / 1.0e9) + " seconds.");
            }
            if ( !arguments.getOutContextVectorsFilename().isEmpty() ) {
                timer = System.nanoTime();
                outputFile = new BufferedWriter(new FileWriter(arguments.getOutContextVectorsFilename()));
                word2VecNeuralNetwork.saveContextVectors(vocabulary, outputFile);
                outputFile.close();
                timer = System.nanoTime() - timer;
                if ( arguments.getDebug() ) {
                    System.out.println("Saving the context vectors took " + (timer / 1.0e9) + " seconds.");
                }
            }
        } catch ( IOException err ) {
            err.printStackTrace();
        }
    }
}
