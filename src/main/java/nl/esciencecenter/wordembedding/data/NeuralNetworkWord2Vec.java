package nl.esciencecenter.wordembedding.data;

import java.util.Random;

public class NeuralNetworkWord2Vec {
    private final boolean CBOW;
    private final boolean hierarchicalSoftmax;
    private final boolean usePosition;
    private final int negativeSamples;
    private final int vectorDimensions;
    private final int windowSize;
    private final float alpha;
    private float samplingFactor = 0.0f;
    // Members that can be modified concurrently
    private long globalWordCount = 0;
    private float currentAlpha;
    private float [] wordVector;
    private float [] outputLayer;
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

    public float [] getOutputLayer() {
        return outputLayer;
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

    public void initialize(Vocabulary vocabulary, int seed) {
        Random randomNumberGenerator = new Random(seed);

        wordVector = new float [vocabulary.getNrWords() * vectorDimensions];
        for ( int index = 0; index < vocabulary.getNrWords() * vectorDimensions; index++ ) {
            wordVector[index] = (((float)(randomNumberGenerator.nextInt()) / (float)(Integer.MAX_VALUE)) - 0.5f)
                / vectorDimensions;
        }
        if ( hierarchicalSoftmax ) {
            outputLayer = new float [vocabulary.getNrWords() * vectorDimensions];
        }
        if ( negativeSamples > 0 ) {
            if (usePosition) {
                contextVector = new float [vocabulary.getNrWords() * vectorDimensions * windowSize * 2];
            } else {
                contextVector = new float [vocabulary.getNrWords() * vectorDimensions];
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

    public float getValueOutputLayer(int item) {
        return outputLayer[item];
    }

    public void setValueOutputLayer(int item, float value) {
        outputLayer[item] = value;
    }

    public void incrementValueOutputLayer(int item, float increment) {
        outputLayer[item] += increment;
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
