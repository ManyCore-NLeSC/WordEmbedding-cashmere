package nl.esciencecenter.wordembedding.commandline;

import com.beust.jcommander.Parameter;

public class Word2VecPMIComparisonCommandLineArguments
{
    @Parameter(names = {"-help", "-h"}, help = true)
    private boolean help = false;
    @Parameter(names = {"-ppmi"}, description = "Use PPMI instead of PMI.")
    private boolean ppmi = false;
    @Parameter(names = {"-vocabulary_file"}, description = "Word2Vec vocabulary file.", required = true)
    private String vocabularyFilename = "";
    @Parameter(names = {"-window"}, description = "Window size for pair generation.")
    private int window = 2;
    @Parameter(names = {"-corpus_file"}, description = "Corpus file.", required = true)
    private String corpusFilename = "";
    @Parameter(names = {"-vector_file"}, description = "Word2Vec vector file.", required = true)
    private String vectorFilename = "";
    @Parameter(names = {"-context_file"}, description = "Word2Vec context file.", required = true)
    private String contextFilename = "";
    // Statistics
    @Parameter(names = {"-max"}, description = "Compute the maximum difference.")
    private boolean max = false;
    @Parameter(names = {"-min"}, description = "Compute the minimum difference.")
    private boolean min = false;
    @Parameter(names = {"-mean"}, description = "Compute the mean difference.")
    private boolean mean = false;
    @Parameter(names = {"-std"}, description = "Compute the standard deviation of differences.")
    private boolean std = false;
    @Parameter(names = {"-histogram"}, description = "Compute the histogram of differences distribution.")
    private boolean histogram = false;

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

    public boolean getMax()
    {
        return max;
    }

    public boolean getMin()
    {
        return min;
    }

    public boolean getMean()
    {
        return mean;
    }

    public boolean getStandardDeviation()
    {
        return std;
    }

    public boolean getHistogram()
    {
        return histogram;
    }
}