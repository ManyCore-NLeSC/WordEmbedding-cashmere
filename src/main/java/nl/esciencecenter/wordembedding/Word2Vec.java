package nl.esciencecenter.wordembedding;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.wordembedding.commandline.Word2VecCommandLineArguments;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.utilities.LearnVocabulary;
import nl.esciencecenter.wordembedding.utilities.ReduceVocabulary;
import nl.esciencecenter.wordembedding.utilities.io.ReadVocabulary;
import nl.esciencecenter.wordembedding.utilities.io.SaveVocabulary;

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

}
