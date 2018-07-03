package nl.esciencecenter.wordembedding;

import nl.esciencecenter.wordembedding.commandline.Word2VecCommandLineArguments;
import nl.esciencecenter.wordembedding.data.ExponentialTable;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.NeuralNetworkWord2Vec;

import java.io.IOException;

class Word2VecCommandLine {

    public static void main(String [] args) {
        long globalTimer = System.nanoTime();
        long timer;
        Vocabulary vocabulary;

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
            timer = System.nanoTime();
            Word2Vec.readVocabulary(vocabulary, arguments.getInVocabularyFilename());
            timer = System.nanoTime() - timer;
            if ( arguments.getDebug() ) {
                System.out.println("Read vocabulary from file \"" + arguments.getInVocabularyFilename() + "\".");
                System.out.println("Reading the vocabulary took " + (timer / 1.0e9) + " seconds.");
                System.out.println("The vocabulary contains " + vocabulary.getNrWords() + " words.");
            }
        } else {
            // Learn vocabulary
            timer = System.nanoTime();
            Word2Vec.learnVocabulary(arguments.getNrThreads(), arguments.getStrict(), vocabulary, arguments.getTrainingFilename());
            timer = System.nanoTime() - timer;
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
        NeuralNetworkWord2Vec neuralNetwork = new NeuralNetworkWord2Vec(arguments.getUseCBOW(), arguments.getSoftmax(),
                arguments.getUsePosition(), arguments.getNegativeSamples(), arguments.getVectorDimensions(),
                arguments.getWindowSize(), arguments.getAlpha());
        ExponentialTable exponentialTable = new ExponentialTable();
        exponentialTable.initialize();
        neuralNetwork.setNrIterations(arguments.getNrIterations());
        try {
            neuralNetwork.initialize(vocabulary, arguments.getSeed(), arguments.getVectorInitializationFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Train neural network
        timer = System.nanoTime();
        for ( int iteration = 0; iteration < arguments.getNrIterations(); iteration++ )
        {
            Word2Vec.trainNetwork(arguments.getNrThreads(), arguments.getThreadSynchronization(), arguments.getProgress(), vocabulary, neuralNetwork, exponentialTable, arguments.getTrainingFilename());
        }
        timer = System.nanoTime() - timer;
        if ( arguments.getDebug() ) {
            System.out.println();
            System.out.println("Training the neural network took " + (timer / 1.0e9) + " seconds.");
            System.out.println("The neural network processed " + String.format("%.2f", (neuralNetwork.getNrIterations() * vocabulary.getOccurrences()) / (timer / 1.0e9)) + " words per second.");
        }
        // Save vocabulary
        if ( arguments.getOutVocabularyFilename().length() > 0 ) {
            timer = System.nanoTime();
            Word2Vec.saveVocabulary(vocabulary, arguments.getOutVocabularyFilename());
            timer = System.nanoTime() - timer;
            if ( arguments.getDebug() ) {
                System.out.println("Saving the vocabulary took " + (timer / 1.0e9) + " seconds.");
            }
        }
        // Save learned vectors
        timer = System.nanoTime();
        Word2Vec.saveWordVectors(arguments.getClasses(), vocabulary, neuralNetwork, arguments.getOutputFilename());
        timer = System.nanoTime() - timer;
        if ( arguments.getDebug() ) {
            System.out.println("Saving the output vectors took " + (timer / 1.0e9) + " seconds.");
        }
        // Save context vectors
        if ( !arguments.getOutContextVectorsFilename().isEmpty() ) {
            timer = System.nanoTime();
            Word2Vec.saveContextVectors(vocabulary, neuralNetwork, arguments.getOutContextVectorsFilename());
            timer = System.nanoTime() - timer;
            if ( arguments.getDebug() ) {
                System.out.println("Saving the context vectors took " + (timer / 1.0e9) + " seconds.");
            }
        }
        globalTimer = System.nanoTime() - globalTimer;
        if ( arguments.getDebug() ) {
            System.out.println("Word2VecCommandLine execution took " + (globalTimer / 1.0e9) + " seconds.");
        }
    }
}
