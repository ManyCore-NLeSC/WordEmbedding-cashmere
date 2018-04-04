package nl.esciencecenter.wordembedding;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.wordembedding.commandline.Word2VecCommandLineArguments;
import nl.esciencecenter.wordembedding.data.ExponentialTable;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.NeuralNetworkWord2Vec;
import nl.esciencecenter.wordembedding.utilities.*;
import nl.esciencecenter.wordembedding.utilities.io.*;

import java.io.*;

class Word2Vec {

    public static void main(String [] args) {
        long globalTimer = System.nanoTime();
        Vocabulary vocabulary;

        // Command line arguments parsing
        Word2VecCommandLineArguments arguments = new Word2VecCommandLineArguments();
        JCommander commander = JCommander.newBuilder().addObject(arguments).build();
        commander.parse(args);
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
            BufferedReader trainingFile;
            LearnVocabulary [] workers = new LearnVocabulary [arguments.getNrThreads()];

            try {
                timer = System.nanoTime();
                trainingFile = new BufferedReader(new FileReader(arguments.getTrainingFilename()));
                for ( int thread = 0; thread < arguments.getNrThreads(); thread++ ) {
                    workers[thread] = new LearnVocabulary(vocabulary, trainingFile, arguments.getStrict());
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
                ReduceVocabulary.reduce(vocabulary);
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
        NeuralNetworkWord2Vec neuralNetwork = new NeuralNetworkWord2Vec(arguments.getUseCBOW(), arguments.getSoftmax(),
                arguments.getUsePosition(), arguments.getNegativeSamples(), arguments.getVectorDimensions(),
                arguments.getWindowSize(), arguments.getAlpha());
        ExponentialTable exponentialTable = new ExponentialTable();
        exponentialTable.initialize();
        neuralNetwork.initialize(vocabulary, arguments.getSeed());
        // Train neural network
        try {
            long timer;
            BufferedReader trainingFile;
            TrainWord2VecModel [] workers = new TrainWord2VecModel [arguments.getNrThreads()];

            timer = System.nanoTime();
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
            timer = System.nanoTime() - timer;
            if ( arguments.getDebug() ) {
                System.out.println();
                System.out.println("Training the neural network took " + (timer / 1.0e9) + " seconds.");
                System.out.println("The neural network processed "
                        + String.format("%.2f", vocabulary.getOccurrences() / (timer / 1.0e9))
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
                SaveWord2VecWordVectors.save(vocabulary, neuralNetwork, outputFile);
            } else {
                SaveWord2VecClasses.save(vocabulary, neuralNetwork, outputFile, arguments.getClasses());
            }
            outputFile.close();
            timer = System.nanoTime() - timer;
            if ( arguments.getDebug() ) {
                System.out.println("Saving the output vectors took " + (timer / 1.0e9) + " seconds.");
            }
            if ( !arguments.getOutContextVectorsFilename().isEmpty() ) {
                timer = System.nanoTime();
                outputFile = new BufferedWriter(new FileWriter(arguments.getOutContextVectorsFilename()));
                SaveWord2VecContextVectors.save(vocabulary, neuralNetwork, outputFile);
                outputFile.close();
                timer = System.nanoTime() - timer;
                if ( arguments.getDebug() ) {
                    System.out.println("Saving the context vectors took " + (timer / 1.0e9) + " seconds.");
                }
            }
        } catch ( IOException err ) {
            err.printStackTrace();
        }
        globalTimer = System.nanoTime() - globalTimer;
        if ( arguments.getDebug() ) {
            System.out.println("Word2Vec execution took " + (globalTimer / 1.0e9) + " seconds.");
        }
    }
}
