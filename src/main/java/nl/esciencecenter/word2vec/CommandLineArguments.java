package nl.esciencecenter.word2vec;

import com.beust.jcommander.Parameter;

public class CommandLineArguments {
    @Parameter(names = {"-help", "-h"}, help = true)
    private Boolean help = false;
    @Parameter(names = {"-training_file", "-train"}, description = "Text file containing the data to train the model.", required = true)
    private String trainingFilename = "";
    @Parameter(names = {"-occurrence_threshold", "-min-count"}, description = "Only consider words that occur more than the threshold; default is 5.")
    private Integer minCount = 5;
    @Parameter(names = {"-store_vocabulary", "-save-vocab"}, description = "File where to store the learned vocabulary.")
    private String outVocabularyFilename = "";
    @Parameter(names = {"-load_vocabulary", "-read-vocab"}, description = "File to load a previously learned vocabulary from.")
    private String inVocabularyFilename = "";
    @Parameter(names = {"-vocabulary_max_size"}, description = "Maximum number of words in the vocabulary.")
    private Integer vocabularyMaxSize = 30000000;
    @Parameter(names = {"-strict"}, description = "Only consider alphanumeric words, and remove punctuation.")
    private Boolean strict = false;
    @Parameter(names = {"-debug"}, description = "Enable debug mode.")
    private Boolean debug = false;
    @Parameter(names = {"-windowSize"}, description = "Window size.")
    private Integer windowSize = 5;
    @Parameter(names = {"-hierarchical_softmax", "-hs"}, description = "Enable Hierarchical Softmax.", arity = 1)
    private Boolean softmax = true;
    @Parameter(names = {"-learning_rate", "-alpha"}, description = "Starting learning rate.")
    private Float alpha = 0.025f;
    @Parameter(names = {"-negative_samples", "-negative"}, description = "Number of negative samples.")
    private Integer negativeSamples = 0;

    public Boolean getHelp() {
        return help;
    }

    public String getTrainingFilename() {
        return trainingFilename;
    }

    public Integer getMinCount() {
        return minCount;
    }

    public String getOutVocabularyFilename() {
        return outVocabularyFilename;
    }

    public String getInVocabularyFilename() {
        return inVocabularyFilename;
    }

    public Integer getVocabularyMaxSize() {
        return vocabularyMaxSize;
    }

    public Boolean getStrict() {
        return strict;
    }

    public Boolean getDebug() {
        return debug;
    }

    public Integer getWindowSize() {
        return windowSize;
    }

    public Boolean getSoftmax() {
        return softmax;
    }

    public Float getAlpha() {
        return alpha;
    }

    public Integer getNegativeSamples() {
        return negativeSamples;
    }
}
