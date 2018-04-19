package nl.esciencecenter.wordembedding;

import ibis.constellation.*;
import nl.esciencecenter.wordembedding.commandline.Word2VecCommandLineArguments;
import nl.esciencecenter.wordembedding.data.ExponentialTable;
import nl.esciencecenter.wordembedding.data.NeuralNetworkWord2Vec;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.utilities.TrainWord2VecModel;
import nl.esciencecenter.wordembedding.utilities.io.*;

import java.io.*;

public class Word2VecConstellation {
    private final static int NR_WORKERS = 1;
    private final static String CONTEXT_ID = "Word2VecContext";

    public static void main(String [] args) {
        int overallEvent;
        Timer overallTimer;
        Constellation constellation;
        Vocabulary vocabulary;

        try {
            constellation = ConstellationFactory.createConstellation(createConfigurations());
        } catch (ConstellationCreationException e) {
            e.printStackTrace();
            return;
        }
        if ( constellation == null ) {
            System.err.println("Impossible to initialize constellation.");
            return;
        }
        overallTimer = constellation.getOverallTimer();
        overallEvent = overallTimer.start();
        // Command line arguments parsing
        Word2VecCommandLineArguments arguments = Word2Vec.parseCommandLine(args);
        if ( arguments == null ) {
            System.err.println("Impossible to parse command line.");
            return;
        }
        vocabulary = new Vocabulary(arguments.getMinCount());
        vocabulary.setMaxSize(arguments.getVocabularyMaxSize());
        if ( arguments.getInVocabularyFilename().length() > 0 ) {
            // Read vocabulary
            int event;
            Timer vocabularyTimer = constellation.getTimer();

            event = vocabularyTimer.start();
            Word2Vec.readVocabulary(vocabulary, arguments.getInVocabularyFilename());
            vocabularyTimer.stop(event);
            if ( arguments.getDebug() ) {
                System.out.println("Read vocabulary from file \"" + arguments.getInVocabularyFilename() + "\".");
                System.out.println("Reading the vocabulary took " + (vocabularyTimer.totalTimeVal() / 1.0e6) + " seconds.");
                System.out.println("The vocabulary contains " + vocabulary.getNrWords() + " words.");
            }
        } else {
            // Learn vocabulary
            int event;
            Timer vocabularyTimer = constellation.getTimer();

            event = vocabularyTimer.start();
            Word2Vec.learnVocabulary(arguments.getNrThreads(), arguments.getStrict(), vocabulary, arguments.getTrainingFilename());
            vocabularyTimer.stop(event);
            if ( arguments.getDebug() ) {
                System.out.println("Learned vocabulary from file \"" + arguments.getTrainingFilename() + "\".");
                System.out.println("Learning the vocabulary took " + (vocabularyTimer.totalTimeVal() / 1.0e6) + " seconds.");
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
        NeuralNetworkWord2Vec neuralNetwork = new NeuralNetworkWord2Vec(arguments.getUseCBOW(), arguments.getSoftmax(),
            arguments.getUsePosition(), arguments.getNegativeSamples(), arguments.getVectorDimensions(),
            arguments.getWindowSize(), arguments.getAlpha());
        ExponentialTable exponentialTable = new ExponentialTable();
        exponentialTable.initialize();
        neuralNetwork.initialize(vocabulary, arguments.getSeed());
        // Train neural network
        try {
            Timer trainingTimer = constellation.getTimer();
            BufferedReader trainingFile;
            TrainWord2VecModel[] workers = new TrainWord2VecModel [arguments.getNrThreads()];

            int event = trainingTimer.start();
            trainingFile = new BufferedReader(new FileReader(arguments.getTrainingFilename()));
            for ( int thread = 0; thread < arguments.getNrThreads(); thread++ ) {
                workers[thread] = new TrainWord2VecModel(vocabulary, neuralNetwork, trainingFile);
                workers[thread].setProgress(arguments.getProgress());
                workers[thread].setExponentialTable(exponentialTable);
                workers[thread].start();
            }
            for ( int thread = 0; thread < arguments.getNrThreads(); thread++ ) {
                try {
                    workers[thread].join();
                } catch ( InterruptedException err ) {
                    err.printStackTrace();
                }
            }
            trainingFile.close();
            trainingTimer.stop(event);
            if ( arguments.getDebug() ) {
                System.out.println();
                System.out.println("Training the neural network took " + (trainingTimer.totalTimeVal() / 1.0e6) + " seconds.");
                System.out.println("The neural network processed "
                    + String.format("%.2f", vocabulary.getOccurrences() / (trainingTimer.totalTimeVal() / 1.0e6))
                    + " words per second.");
            }
        } catch ( IOException err ) {
            err.printStackTrace();
        }
        // Save vocabulary
        if ( arguments.getOutVocabularyFilename().length() > 0 ) {
            Timer vocabularyTimer = constellation.getTimer();

            int event = vocabularyTimer.start();
            Word2Vec.saveVocabulary(vocabulary, arguments.getOutVocabularyFilename());
            vocabularyTimer.stop(event);
            if ( arguments.getDebug() ) {
                System.out.println("Saving the vocabulary took " + (vocabularyTimer.totalTimeVal() / 1.0e6) + " seconds.");
            }
        }
        // Save learned vectors
        BufferedWriter outputFile;
        try {
            int event;
            Timer outputTimer = constellation.getTimer();
            outputFile = new BufferedWriter(new FileWriter(arguments.getOutputFilename()));

            event = outputTimer.start();
            if ( arguments.getClasses() == 0 ) {
                SaveWord2VecWordVectors.save(vocabulary, neuralNetwork, outputFile);
            } else {
                SaveWord2VecClasses.save(vocabulary, neuralNetwork, outputFile, arguments.getClasses());
            }
            outputFile.close();
            outputTimer.stop(event);
            if ( arguments.getDebug() ) {
                System.out.println("Saving the output vectors took " + (outputTimer.totalTimeVal() / 1.0e6) + " seconds.");
            }
            if ( !arguments.getOutContextVectorsFilename().isEmpty() ) {
                outputTimer = constellation.getTimer();
                event = outputTimer.start();
                outputFile = new BufferedWriter(new FileWriter(arguments.getOutContextVectorsFilename()));
                SaveWord2VecContextVectors.save(vocabulary, neuralNetwork, outputFile);
                outputFile.close();
                outputTimer.stop(event);
                if ( arguments.getDebug() ) {
                    System.out.println("Saving the context vectors took " + (outputTimer.totalTimeVal() / 1.0e6) + " seconds.");
                }
            }
        } catch ( IOException err ) {
            err.printStackTrace();
        }
        overallTimer.stop(overallEvent);
        if ( arguments.getDebug() ) {
            System.out.println("Word2VecCommandLine execution took " + (overallTimer.averageTimeVal() / 1.0e6) + " seconds.");
        }
    }

    private static ConstellationConfiguration [] createConfigurations() {
        ConstellationConfiguration [] configurations = new ConstellationConfiguration [NR_WORKERS];

        for ( int worker = 0; worker < NR_WORKERS; worker++ ) {
            configurations[worker] = new ConstellationConfiguration(new Context(CONTEXT_ID));
        }
        return configurations;
    }
}
