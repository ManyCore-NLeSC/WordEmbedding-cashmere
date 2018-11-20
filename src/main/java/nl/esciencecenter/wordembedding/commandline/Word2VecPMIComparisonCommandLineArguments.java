package nl.esciencecenter.wordembedding.commandline;

import com.beust.jcommander.Parameter;

public class Word2VecPMIComparisonCommandLineArguments
{
    @Parameter(names = {"-help", "-h"}, help = true)
    private boolean help = false;
    @Parameter(names = {"-word2vec"}, description = "Compute results for Word2Vec.")
    private boolean word2vec = false;
    @Parameter(names = {"-pmi"}, description = "Compute results for PMI.")
    private boolean pmi = false;
    @Parameter(names = {"-difference"}, description = "Compute difference between Word2Vec and PMI.")
    private boolean difference = false;
    @Parameter(names = {"-ppmi"}, description = "Use PPMI instead of PMI.")
    private boolean ppmi = false;
    @Parameter(names = {"-vocabulary_file"}, description = "Word2Vec vocabulary file.", required = true)
    private String vocabularyFilename = "";
    @Parameter(names = {"-window"}, description = "Window size for pair generation.")
    private int window = 2;
    @Parameter(names = {"-min_occurrences"}, description = "Threshold for the vocabulary.")
    private int minOccurrences = 0;
    @Parameter(names = {"-corpus_file"}, description = "Corpus file.")
    private String corpusFilename = "";
    @Parameter(names = {"-vector_file"}, description = "Word2Vec vector file.")
    private String vectorFilename = "";
    @Parameter(names = {"-context_file"}, description = "Word2Vec context file.")
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
    @Parameter(names = {"-histogram_size"}, description = "Number of bins in the histogram.")
    private int histogramSize = 100;
    @Parameter(names = {"-spearman"}, description = "Compute the Spearman correlation between Word2Vec and PMI/PPMI.")
    private boolean spearman = false;

    public boolean getHelp()
    {
        return help;
    }

    public boolean getWord2Vec()
    {
        return word2vec;
    }

    public boolean getPMI()
    {
        return pmi;
    }

    public boolean getDifference()
    {
        return difference;
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

    public int getMinOccurrences()
    {
        return minOccurrences;
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

    public int getHistogramSize()
    {
        return histogramSize;
    }

    public boolean getSpearman()
    {
        return spearman;
    }
}