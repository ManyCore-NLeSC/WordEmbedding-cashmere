package nl.esciencecenter.wordembedding.commandline;

import com.beust.jcommander.Parameter;

public class Word2VecCommandLineArguments {
    @Parameter(names = {"-help", "-h"}, help = true)
    private boolean help = false;
    @Parameter(names = {"-training_file", "-train"}, description = "Text file containing the data to train the model.",
            required = true)
    private String trainingFilename = "";
    @Parameter(names = {"-output_file", "-output"}, description = "Output file.", required = true)
    private String outputFilename = "";
    @Parameter(names = {"-occurrence_threshold", "-min-count"},
            description = "Only consider words that occur more than the threshold; default is 5.")
    private int minCount = 5;
    @Parameter(names = {"-store_vocabulary", "-save-vocab"},
            description = "File where to store the learned vocabulary.")
    private String outVocabularyFilename = "";
    @Parameter(names = {"-load_vocabulary", "-read-vocab"},
            description = "File to load a previously learned vocabulary from.")
    private String inVocabularyFilename = "";
    @Parameter(names = {"-vocabulary_max_size"}, description = "Maximum number of words in the vocabulary.")
    private int vocabularyMaxSize = 30000000;
    @Parameter(names = {"-strict"}, description = "Only consider alphanumeric words, and remove punctuation.")
    private boolean strict = false;
    @Parameter(names = {"-debug"}, description = "Enable debug mode.")
    private boolean debug = false;
    @Parameter(names = {"-progress"}, description = "Print progress message.")
    private boolean progress = false;
    @Parameter(names = {"-windowSize"}, description = "Window size.")
    private int windowSize = 5;
    @Parameter(names = {"-no_hierarchical_softmax", "-hs"}, description = "Disable Hierarchical Softmax.")
    private boolean softmax = true;
    @Parameter(names = {"-learning_rate", "-alpha"}, description = "Starting learning rate.")
    private float alpha = 0.025f;
    @Parameter(names = {"-negative_samples", "-negative"}, description = "Number of negative samples.")
    private int negativeSamples = 0;
    @Parameter(names = {"-cbow"}, description = "Use the continuous bag of words (CBOW) model.")
    private boolean useCBOW = false;
    @Parameter(names = {"-use_position", "-pos"}, description = "Use position for model training.")
    private boolean usePosition = false;
    @Parameter(names = {"-vector_dimensions", "-size"}, description = "Dimensions of the word vectors.")
    private int vectorDimensions = 100;
    @Parameter(names = {"-sampling", "-sample"}, description = "Use sampling for high-frequency words.")
    private float samplingFactor = 0.0f;
    @Parameter(names = {"-classes"}, description = "Use classes vectors instead of word vectors.")
    private int classes = 0;
    @Parameter(names = {"-binary_output", "-binary"}, description = "Use binary mode for output vectors.")
    private boolean binaryMode = false;
    @Parameter(names = {"-store_context_vectors", "-dumpcv"}, description = "File where to store the context vectors.")
    private String outContextVectorsFilename = "";
    @Parameter(names = {"-threads"}, description = "Number of threads.")
    private int threads = 1;
    @Parameter(names = {"-synchronize", "-sync"}, description = "Thread synchronization during training.")
    private boolean threadSynchronization = false;
    @Parameter(names = {"-seed"}, description = "The seed for the random number generator.")
    private int seed = 1;
    @Parameter(names = {"-pinit"}, description = "Read the word vectors initialization from the specified file.")
    private String vectorInitializationFile = "";
    @Parameter(names = {"-iters", "-iterations"}, description = "Number of iterations over the training file.")
    private int nrIterations = 1;

    public boolean getHelp() {
        return help;
    }

    public String getTrainingFilename() {
        return trainingFilename;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public int getMinCount() {
        return minCount;
    }

    public String getOutVocabularyFilename() {
        return outVocabularyFilename;
    }

    public String getInVocabularyFilename() {
        return inVocabularyFilename;
    }

    public int getVocabularyMaxSize() {
        return vocabularyMaxSize;
    }

    public boolean getStrict() {
        return strict;
    }

    public boolean getDebug() {
        return debug;
    }

    public boolean getProgress() {
        return progress;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public boolean getSoftmax() {
        return softmax;
    }

    public float getAlpha() {
        return alpha;
    }

    public int getNegativeSamples() {
        return negativeSamples;
    }

    public boolean getUseCBOW() {
        return useCBOW;
    }

    public boolean getUsePosition() {
        return usePosition;
    }

    public int getVectorDimensions() {
        return vectorDimensions;
    }

    public float getSamplingFactor() {
        return samplingFactor;
    }

    public int getClasses() {
        return classes;
    }

    public boolean getBinaryMode() {
        return binaryMode;
    }

    public String getOutContextVectorsFilename() {
        return outContextVectorsFilename;
    }

    public int getNrThreads() {
        return threads;
    }

    public boolean getThreadSynchronization()
    {
        return threadSynchronization;
    }

    public int getSeed() {
        return seed;
    }

    public String getVectorInitializationFile() {
        return vectorInitializationFile;
    }

    public int getNrIterations()
    {
        return nrIterations;
    }
}
