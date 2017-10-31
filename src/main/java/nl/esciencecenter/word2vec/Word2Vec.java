package nl.esciencecenter.word2vec;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.word2vec.data.Vocabulary;
import nl.esciencecenter.word2vec.utilities.LearnVocabulary;
import nl.esciencecenter.word2vec.utilities.ReadVocabulary;
import nl.esciencecenter.word2vec.utilities.SaveVocabulary;

import java.io.*;

public class Word2Vec {

    public static void main(String [] argv) {
        Vocabulary vocabulary;

        // Command line arguments parsing
        CommandLineArguments arguments = new CommandLineArguments();
        JCommander commander = JCommander.newBuilder().addObject(arguments).build();
        commander.parse(argv);
        if ( arguments.getHelp() ) {
            commander.usage();
            return;
        }

        vocabulary = new Vocabulary(arguments.getMinCount());
        vocabulary.setMaxSize(arguments.getVocabularyMaxSize());

        if ( arguments.getInVocabularyFilename().length() > 0 ) {
            BufferedReader inVocabularyFile;

            try {
                inVocabularyFile = new BufferedReader(new FileReader(arguments.getInVocabularyFilename()));
                ReadVocabulary.read(vocabulary, inVocabularyFile);
                inVocabularyFile.close();
            }catch ( IOException err) {
                err.printStackTrace();
            }
            if ( arguments.getDebug() ) {
                System.err.println("Read vocabulary from file \"" + arguments.getInVocabularyFilename() + "\"; the vocabulary contains " + vocabulary.getNrWords() + " words.");
            }
        } else {
            BufferedReader trainingFile;

            try {
                trainingFile = new BufferedReader(new FileReader(arguments.getTrainingFilename()));
                LearnVocabulary.learn(vocabulary, trainingFile, arguments.getStrict());
                trainingFile.close();
            } catch ( IOException err ) {
                err.printStackTrace();
            }
            if ( arguments.getDebug() ) {
                System.err.println("Learned vocabulary from file \"" + arguments.getTrainingFilename() + "\"; the vocabulary contains " + vocabulary.getNrWords() + " words.");
            }
        }

        if ( arguments.getOutVocabularyFilename().length() > 0 ) {
            BufferedWriter outVocabularyFile;

            try {
                outVocabularyFile = new BufferedWriter(new FileWriter(arguments.getOutVocabularyFilename()));
                SaveVocabulary.save(vocabulary, outVocabularyFile);
                outVocabularyFile.close();
            } catch ( IOException err ) {
                err.printStackTrace();
            }
        }
    }
}
