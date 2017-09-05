package nl.esciencecenter.word2vec;

import com.beust.jcommander.Parameter;

public class CommandLineArguments {
    @Parameter(names = {"-training_file", "-train"}, description = "Text file containing the data to train the model.", required = true)
    private String trainingFilename = "";
    @Parameter(names = {"-occurrence_threshold", "-min-count"}, description = "Only consider words that occur more than the threshold; default is 5.")
    private Integer minCount = 5;
    @Parameter(names = {"-store_vocabulary", "-save-vocab"}, description = "File where to store the learned vocabulary.")
    private String outVocabularyFilename = "";
    @Parameter(names = {"-load_vocabulary", "-read-vocab"}, description = "File to load a previously learned vocabulary from.")
    private String inVocabularyFilename = "";

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
}
