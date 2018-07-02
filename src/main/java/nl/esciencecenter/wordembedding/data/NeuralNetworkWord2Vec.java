package nl.esciencecenter.wordembedding.data;

import nl.esciencecenter.wordembedding.utilities.io.ReadWord2VecContextVectors;
import nl.esciencecenter.wordembedding.utilities.io.ReadWord2VecWordVectors;

import java.io.IOException;
import java.util.Random;

public class NeuralNetworkWord2Vec {
    private final boolean CBOW;
    private final boolean hierarchicalSoftmax;
    private final boolean usePosition;
    private final int negativeSamples;
    private final int vectorDimensions;
    private final int windowSize;
    private int nrIterations = 1;
    private final float alpha;
    private float samplingFactor = 0.0f;
    private long globalWordCount = 0;
    private float currentAlpha;
    private float [] wordVector;
    private float [] hierarchicalSoftMaxLayer;
    private float [] contextVector;

    public NeuralNetworkWord2Vec(boolean CBOW, boolean hierarchicalSoftmax, boolean usePosition, int negativeSamples,
                                 int vectorDimensions, int windowSize, float alpha) {
        this.CBOW = CBOW;
        this.hierarchicalSoftmax = hierarchicalSoftmax;
        this.usePosition = usePosition;
        this.negativeSamples = negativeSamples;
        this.vectorDimensions = vectorDimensions;
        this.windowSize = windowSize;
        this.alpha = alpha;
        this.currentAlpha = alpha;
    }

    public boolean getSkipGram() {
        return !CBOW;
    }

    public boolean getCBOW() {
        return CBOW;
    }

    public boolean getHierarchicalSoftmax() {
        return hierarchicalSoftmax;
    }

    public boolean getUsePosition() {
        return usePosition;
    }

    public int getNegativeSamples() {
        return negativeSamples;
    }

    public int getVectorDimensions() {
        return vectorDimensions;
    }

    public void setSamplingFactor(float samplingFactor) {
        this.samplingFactor = samplingFactor;
    }

    public float getSamplingFactor() {
        return samplingFactor;
    }

    public float [] getWordVector() {
        return wordVector;
    }

    public float [] getHierarchicalSoftMaxLayer() {
        return hierarchicalSoftMaxLayer;
    }

    public float [] getContextVector() {
        return contextVector;
    }

    public float getAlpha() {
        return alpha;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setNrIterations(int nrIterations)
    {
        this.nrIterations = nrIterations;
    }

    public int getNrIterations()
    {
        return nrIterations;
    }

    public void initialize(Vocabulary vocabulary, int seed, String wordVectorFilename) throws IOException
    {
        Random randomNumberGenerator = new Random(seed);

        if (wordVectorFilename.equals(""))
        {
            wordVector = new float [vocabulary.getNrWords() * vectorDimensions];
            for ( int index = 0; index < vocabulary.getNrWords() * vectorDimensions; index++ ) {
                wordVector[index] = (((float)(randomNumberGenerator.nextInt()) / (float)(Integer.MAX_VALUE)) - 0.5f)
                    / vectorDimensions;
            }
        }
        else
        {
            if (wordVectorFilename.contains(".txt"))
            {
                wordVector = ReadWord2VecWordVectors.read(wordVectorFilename);
            }
            else
            {
                wordVector = ReadWord2VecWordVectors.read(wordVectorFilename, vocabulary.getNrWords() * vectorDimensions);
            }
        }
        if ( hierarchicalSoftmax )
        {
            hierarchicalSoftMaxLayer = new float [vocabulary.getNrWords() * vectorDimensions];
        }
        if ( negativeSamples > 0 )
        {
            if (usePosition)
            {
                contextVector = new float [vocabulary.getNrWords() * vectorDimensions * windowSize * 2];
            }
            else
            {
                if (wordVectorFilename.equals(""))
                {
                    contextVector = new float [vocabulary.getNrWords() * vectorDimensions];
                }
                else
                {
                    if (wordVectorFilename.contains(".txt"))
                    {
                        contextVector = ReadWord2VecContextVectors.read(wordVectorFilename, (long)(vocabulary.getNrWords() * vectorDimensions));
                    }
                    else
                    {
                        contextVector = ReadWord2VecContextVectors.read(wordVectorFilename, vocabulary.getNrWords() * vectorDimensions, (long)(vocabulary.getNrWords() * vectorDimensions));
                    }
                }
            }
        }
        vocabulary.generateCodes();
    }

    public float getValueWordVector(int item) {
        return wordVector[item];
    }

    public void setValueWordVector(int item, float value) {
        wordVector[item] = value;
    }

    public void incrementValueWordVector(int item, float increment) {
        wordVector[item] += increment;
    }

    public float getValueHierarchicalSoftMaxLayer(int item) {
        return hierarchicalSoftMaxLayer[item];
    }

    public void setValueHierarchicalSoftMaxLayer(int item, float value) {
        hierarchicalSoftMaxLayer[item] = value;
    }

    public void incrementValueHierarchicalSoftMaxLayer(int item, float increment) {
        hierarchicalSoftMaxLayer[item] += increment;
    }

    public float getValueContextVector(int item) {
        return contextVector[item];
    }

    public void setValueContextVector(int item, float value) {
        contextVector[item] = value;
    }

    public void incrementValueContextVector(int item, float increment) {
        contextVector[item] += increment;
    }

    public long getGlobalWordCount() {
        return globalWordCount;
    }

    public void incrementGlobalWordCount(long increment) {
        globalWordCount += increment;
    }

    public float getCurrentAlpha() {
        return currentAlpha;
    }

    public void setCurrentAlpha(float alpha) {
        currentAlpha = alpha;
    }
}
