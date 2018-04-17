package nl.esciencecenter.wordembedding;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.wordembedding.commandline.Word2VecCommandLineArguments;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.utilities.io.ReadVocabulary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
        BufferedReader inVocabularyFile;

        try {
            inVocabularyFile = new BufferedReader(new FileReader(filename));
            ReadVocabulary.read(vocabulary, inVocabularyFile);
            inVocabularyFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
