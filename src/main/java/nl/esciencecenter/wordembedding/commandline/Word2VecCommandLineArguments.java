package nl.esciencecenter.wordembedding.commandline;

import com.beust.jcommander.Parameter;

public class Word2VecCommandLineArguments {
    @Parameter(names = {"-help", "-h"}, help = true)
    private Boolean help = false;
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
    private Boolean strict = false;
    @Parameter(names = {"-debug"}, description = "Enable debug mode.")
    private Boolean debug = false;
    @Parameter(names = {"-windowSize"}, description = "Window size.")
    private int windowSize = 5;
    @Parameter(names = {"-hierarchical_softmax", "-hs"}, description = "Enable Hierarchical Softmax.", arity = 1)
    private Boolean softmax = true;
    @Parameter(names = {"-learning_rate", "-alpha"}, description = "Starting learning rate.")
    private float alpha = 0.025f;
    @Parameter(names = {"-negative_samples", "-negative"}, description = "Number of negative samples.")
    private int negativeSamples = 0;
    @Parameter(names = {"-cbow"}, description = "Use the continuous bag of words (CBOW) model.")
    private Boolean useCBOW = false;
    @Parameter(names = {"-use_position", "-pos"}, description = "Use position for model training.")
    private Boolean usePosition = false;
    @Parameter(names = {"-vector_dimensions", "-size"}, description = "Dimensions of the word vectors.")
    private int vectorDimensions = 100;
    @Parameter(names = {"-sampling", "-sample"}, description = "Use sampling for high-frequency words.")
    private float samplingFactor = 0.0f;
    @Parameter(names = {"-classes"}, description = "Use classes vectors instead of word vectors.")
    private int classes = 0;
    @Parameter(names = {"-binary_output", "-binary"}, description = "Use binary mode for output vectors.")
    private Boolean binaryMode = false;
    @Parameter(names = {"-store_context_vectors", "-dumpcv"}, description = "File where to store the context vectors.")
    private String outContextVectorsFilename = "";
    @Parameter(names = {"-threads"}, description = "Number of threads.")
    private int threads = 1;
    @Parameter(names = {"-seed"}, description = "The seed for the random number generator.")
    private int seed = 1;

    public Boolean getHelp() {
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

    public Boolean getStrict() {
        return strict;
    }

    public Boolean getDebug() {
        return debug;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public Boolean getSoftmax() {
        return softmax;
    }

    public float getAlpha() {
        return alpha;
    }

    public int getNegativeSamples() {
        return negativeSamples;
    }

    public Boolean getUseCBOW() {
        return useCBOW;
    }

    public Boolean getUsePosition() {
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

    public Boolean getBinaryMode() {
        return binaryMode;
    }

    public String getOutContextVectorsFilename() {
        return outContextVectorsFilename;
    }

    public int getNrThreads() {
        return threads;
    }

    public int getSeed() {
        return seed;
    }
}
