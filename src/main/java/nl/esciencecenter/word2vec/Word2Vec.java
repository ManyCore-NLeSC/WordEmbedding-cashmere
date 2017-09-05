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
        JCommander.newBuilder().addObject(arguments).build().parse(argv);

        vocabulary = new Vocabulary(arguments.getMinCount());
        vocabulary.setMaxSize(arguments.getVocabularyMaxSize());

        if ( arguments.getInVocabularyFilename().length() > 0 ) {
            ReadVocabulary reader = new ReadVocabulary();
            BufferedReader inVocabularyFile;

            try {
                inVocabularyFile = new BufferedReader(new FileReader(arguments.getInVocabularyFilename()));
                reader.read(vocabulary, inVocabularyFile);
                inVocabularyFile.close();
            }catch ( IOException err) {
                err.printStackTrace();
            }
        } else {
            LearnVocabulary learner = new LearnVocabulary();
            BufferedReader trainingFile;

            try {
                trainingFile = new BufferedReader(new FileReader(arguments.getTrainingFilename()));
                learner.learn(vocabulary, trainingFile);
                trainingFile.close();
            } catch ( IOException err ) {
                err.printStackTrace();
            }
        }

        if ( arguments.getOutVocabularyFilename().length() > 0 ) {
            SaveVocabulary writer = new SaveVocabulary();
            BufferedWriter outVocabularyFile;

            try {
                outVocabularyFile = new BufferedWriter(new FileWriter(arguments.getOutVocabularyFilename()));
                writer.save(vocabulary, outVocabularyFile);
                outVocabularyFile.close();
            } catch ( IOException err ) {
                err.printStackTrace();
            }
        }
    }
}
