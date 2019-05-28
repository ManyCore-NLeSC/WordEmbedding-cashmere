package nl.esciencecenter.wordembedding.commandline;

import com.beust.jcommander.Parameter;

public class Word2VecPMIComparisonCommandLineArguments {
    @Parameter(names = {"-help", "-h"}, help = true)
    private boolean help = false;
    @Parameter(names = {"-ppmi"}, description = "Use PPMI instead of PMI.")
    private boolean ppmi = false;
    @Parameter(names = {"-window"}, description = "Window size for pair generation.")
    private int window = 2;
    @Parameter(names = {"-negative_samples"}, description = "Negative samples used for Word2Vec.")
    private int negativeSamples = 1;
    @Parameter(names = {"-sampling_rate"}, description = "Probability that a word is removed from the vocabulary.")
    private int samplingRate = 0;
    @Parameter(names = {"-max_pairs"}, description = "Maximum number of pairs.")
    private long maxPairs = 0;
    // Files
    @Parameter(names = {"-vocabulary_file"}, description = "Word2Vec vocabulary file.", required = true)
    private String vocabularyFilename = "";
    @Parameter(names = {"-corpus_file"}, description = "Corpus file.", required = true)
    private String corpusFilename = "";
    @Parameter(names = {"-vector_file"}, description = "Word2Vec vector file.", required = true)
    private String vectorFilename = "";
    @Parameter(names = {"-context_file"}, description = "Word2Vec context file.", required = true)
    private String contextFilename = "";
    // Options
    @Parameter(names = {"-distance"}, description = "Compute the Euclidean distance between matrices.")
    private boolean distance = false;
    @Parameter(names = {"-deviation"}, description = "Compute the objective function deviation.")
    private boolean deviation = false;
    @Parameter(names = {"-sampling"}, description = "Sample the negative samples in the vocabulary.")
    private boolean sampling = false;
    @Parameter(names = {"-fraction"}, description = "Use only a fraction of the words for the negative sampling.")
    private float samplingFraction = 1.0f;
    @Parameter(names = {"-frobenius"}, description = "Compute the Frobenius norm of the matrices.")
    private boolean frobenius = false;

    public boolean getHelp() {
        return help;
    }

    public boolean getPPMI() {
        return ppmi;
    }

    public String getVocabularyFileName() {
        return vocabularyFilename;
    }

    public int getWindow() {
        return window;
    }

    public int getNegativeSamples() {
        return negativeSamples;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public long getMaxPairs() {
        return maxPairs;
    }

    public String getCorpusFileName() {
        return corpusFilename;
    }

    public String getVectorFileName() {
        return vectorFilename;
    }

    public String getContextFileName() {
        return contextFilename;
    }

    public boolean getDistance() {
        return distance;
    }

    public boolean getDeviation() {
        return deviation;
    }

    public boolean getSampling()
    {
        return sampling;
    }

    public float getSamplingFraction()
    {
        return samplingFraction;
    }

    public boolean getFrobenius()
    {
        return frobenius;
    }
}