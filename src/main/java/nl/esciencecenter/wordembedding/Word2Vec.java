package nl.esciencecenter.wordembedding;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.wordembedding.commandline.Word2VecCommandLineArguments;
import nl.esciencecenter.wordembedding.data.ExponentialTable;
import nl.esciencecenter.wordembedding.data.NeuralNetworkWord2Vec;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.utilities.LearnVocabulary;
import nl.esciencecenter.wordembedding.utilities.ReduceVocabulary;
import nl.esciencecenter.wordembedding.utilities.io.ReadVocabulary;
import nl.esciencecenter.wordembedding.utilities.io.SaveVocabulary;
import nl.esciencecenter.wordembedding.utilities.io.SaveWord2VecClasses;
import nl.esciencecenter.wordembedding.utilities.io.SaveWord2VecWordVectors;
import nl.esciencecenter.wordembedding.utilities.io.SaveWord2VecContextVectors;
import nl.esciencecenter.wordembedding.utilities.TrainWord2VecModel;

import java.io.*;

class Word2Vec {

    static Word2VecCommandLineArguments parseCommandLine(String [] args) {
        Word2VecCommandLineArguments arguments = new Word2VecCommandLineArguments();
        JCommander commander = JCommander.newBuilder().addObject(arguments).build();
        commander.parse(args);
        if ( arguments.getHelp() ) {
            commander.usage();
            return null;
        }
        return arguments;
    }

    static void readVocabulary(Vocabulary vocabulary, String filename) {
        try {
            BufferedReader inVocabularyFile = new BufferedReader(new FileReader(filename));
            ReadVocabulary.read(vocabulary, inVocabularyFile);
            inVocabularyFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void learnVocabulary(int nrThreads, boolean strict, Vocabulary vocabulary, String filename) {
        BufferedReader trainingFile;
        LearnVocabulary[] workers = new LearnVocabulary [nrThreads];

        try {
            trainingFile = new BufferedReader(new FileReader(filename));
            for ( int thread = 0; thread < nrThreads; thread++ ) {
                workers[thread] = new LearnVocabulary(vocabulary, trainingFile, strict);
                workers[thread].start();
            }
            for ( int thread = 0; thread < nrThreads; thread++ ) {
                try {
                    workers[thread].join();
                } catch ( InterruptedException err ) {
                    err.printStackTrace();
                }
            }
            trainingFile.close();
            ReduceVocabulary.reduce(vocabulary);
        } catch ( IOException err ) {
            err.printStackTrace();
        }
    }

    static void saveVocabulary(Vocabulary vocabulary, String filename) {
        try {
            BufferedWriter outVocabularyFile = new BufferedWriter(new FileWriter(filename));
            SaveVocabulary.save(vocabulary, outVocabularyFile);
            outVocabularyFile.close();
        } catch ( IOException err ) {
            err.printStackTrace();
        }
    }

    static void saveVectors(int nrClasses, Vocabulary vocabulary, NeuralNetworkWord2Vec neuralNetwork, String filename) {
        try {
            BufferedWriter outputFile = new BufferedWriter(new FileWriter(filename));
            if ( nrClasses == 0 ) {
                SaveWord2VecWordVectors.save(vocabulary, neuralNetwork, outputFile);
            } else {
                SaveWord2VecClasses.save(vocabulary, neuralNetwork, outputFile, nrClasses);
            }
            outputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void saveContext(Vocabulary vocabulary, NeuralNetworkWord2Vec neuralNetwork, String filename) {
        try {
            BufferedWriter outputFile = new BufferedWriter(new FileWriter(filename));
            SaveWord2VecContextVectors.save(vocabulary, neuralNetwork, outputFile);
            outputFile.close();
        } catch ( IOException err ) {
            err.printStackTrace();
        }
    }

    static void trainNetwork(int nrThreads, boolean showProgress, Vocabulary vocabulary, NeuralNetworkWord2Vec neuralNetwork, ExponentialTable exponentialTable, String filename) {
        try {
            BufferedReader trainingFile;
            TrainWord2VecModel [] workers = new TrainWord2VecModel [nrThreads];

            trainingFile = new BufferedReader(new FileReader(filename));
            for ( int thread = 0; thread < nrThreads; thread++ ) {
                workers[thread] = new TrainWord2VecModel(vocabulary, neuralNetwork, trainingFile);
                workers[thread].setProgress(showProgress);
                workers[thread].setExponentialTable(exponentialTable);
                workers[thread].start();
            }
            for ( int thread = 0; thread < nrThreads; thread++ ) {
                try {
                    workers[thread].join();
                } catch ( InterruptedException err ) {
                    err.printStackTrace();
                }
            }
            trainingFile.close();
        } catch ( IOException err ) {
            err.printStackTrace();
        }
    }
}
