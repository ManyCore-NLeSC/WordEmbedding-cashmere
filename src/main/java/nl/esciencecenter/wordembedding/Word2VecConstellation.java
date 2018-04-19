package nl.esciencecenter.wordembedding;

import ibis.constellation.*;
import nl.esciencecenter.wordembedding.commandline.Word2VecCommandLineArguments;
import nl.esciencecenter.wordembedding.data.ExponentialTable;
import nl.esciencecenter.wordembedding.data.NeuralNetworkWord2Vec;
import nl.esciencecenter.wordembedding.data.Vocabulary;

public class Word2VecConstellation {
    private final static int NR_WORKERS = 1;
    private final static String CONTEXT_ID = "Word2VecContext";

    public static void main(String [] args) {
        int overallEvent;
        int event;
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
        Timer trainingTimer = constellation.getTimer();
        event = trainingTimer.start();
        Word2Vec.trainNetwork(arguments.getNrThreads(), arguments.getProgress(), vocabulary, neuralNetwork, exponentialTable, arguments.getTrainingFilename());
        trainingTimer.stop(event);
        if ( arguments.getDebug() ) {
            System.out.println();
            System.out.println("Training the neural network took " + (trainingTimer.totalTimeVal() / 1.0e6) + " seconds.");
            System.out.println("The neural network processed " + String.format("%.2f", vocabulary.getOccurrences() / (trainingTimer.totalTimeVal() / 1.0e6)) + " words per second.");
        }
        // Save vocabulary
        if ( arguments.getOutVocabularyFilename().length() > 0 ) {
            Timer vocabularyTimer = constellation.getTimer();

            event = vocabularyTimer.start();
            Word2Vec.saveVocabulary(vocabulary, arguments.getOutVocabularyFilename());
            vocabularyTimer.stop(event);
            if ( arguments.getDebug() ) {
                System.out.println("Saving the vocabulary took " + (vocabularyTimer.totalTimeVal() / 1.0e6) + " seconds.");
            }
        }
        // Save learned vectors
        Timer outputTimer = constellation.getTimer();

        event = outputTimer.start();
        Word2Vec.saveVectors(arguments.getClasses(), vocabulary, neuralNetwork, arguments.getOutputFilename());
        outputTimer.stop(event);
        if ( arguments.getDebug() ) {
            System.out.println("Saving the output vectors took " + (outputTimer.totalTimeVal() / 1.0e6) + " seconds.");
        }
        // Save context vectors
        if ( !arguments.getOutContextVectorsFilename().isEmpty() ) {
            event = outputTimer.start();
            Word2Vec.saveContext(vocabulary, neuralNetwork, arguments.getOutContextVectorsFilename());
            outputTimer.stop(event);
            if ( arguments.getDebug() ) {
                System.out.println("Saving the context vectors took " + (outputTimer.totalTimeVal() / 1.0e6) + " seconds.");
            }
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
