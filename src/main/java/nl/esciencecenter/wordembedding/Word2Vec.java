package nl.esciencecenter.wordembedding;

import com.beust.jcommander.JCommander;
import nl.esciencecenter.wordembedding.commandline.Word2VecCommandLineArguments;

public class Word2Vec {

    public static Word2VecCommandLineArguments parseCommandLine(String [] args) {
        Word2VecCommandLineArguments arguments = new Word2VecCommandLineArguments();
        JCommander commander = JCommander.newBuilder().addObject(arguments).build();
        commander.parse(args);
        if ( arguments.getHelp() ) {
            commander.usage();
            return null;
        }
        return arguments;
    }

}
