package nl.esciencecenter.wordembedding.commandline;

import com.beust.jcommander.Parameter;

public class Word2VecPMIComparisonCommandLineArguments
{
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
    private int maxPairs = 0;
    // Files
    @Parameter(names = {"-vocabulary_file"}, description = "Word2Vec vocabulary file.", required = true)
    private String vocabularyFilename = "";
    @Parameter(names = {"-corpus_file"}, description = "Corpus file.", required = true)
    private String corpusFilename = "";
    @Parameter(names = {"-vector_file"}, description = "Word2Vec vector file.", required = true)
    private String vectorFilename = "";
    @Parameter(names = {"-context_file"}, description = "Word2Vec context file.", required = true)
    private String contextFilename = "";

    public boolean getHelp()
    {
        return help;
    }

    public boolean getPPMI()
    {
        return ppmi;
    }

    public String getVocabularyFileName()
    {
        return vocabularyFilename;
    }

    public int getWindow()
    {
        return window;
    }

    public int getNegativeSamples()
    {
        return negativeSamples;
    }

    public int getSamplingRate()
    {
        return samplingRate;
    }

    public int getMaxPairs()
    {
        return maxPairs;
    }

    public String getCorpusFileName()
    {
        return corpusFilename;
    }

    public String getVectorFileName()
    {
        return vectorFilename;
    }

    public String getContextFileName()
    {
        return contextFilename;
    }
}