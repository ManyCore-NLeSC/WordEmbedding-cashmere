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
    private float [] inputLayer;
    private float [] outputLayer;
    private float [] outputLayerNegativeSamples;

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

    public float [] getInputLayer() {
        return inputLayer;
    }

    public float [] getOutputLayer() {
        return outputLayer;
    }

    public float [] getOutputLayerNegativeSamples() {
        return outputLayerNegativeSamples;
    }

    public float getAlpha() {
        return alpha;
    }

    public int getWindowSize() {
        return windowSize;
    }

    //
    // Synchronized methods
    //
    public synchronized void initialize(Vocabulary vocabulary, int seed) {
        Random randomNumberGenerator = new Random(seed);

        inputLayer = new float [vocabulary.getNrWords() * vectorDimensions];
        for ( int index = 0; index < vocabulary.getNrWords() * vectorDimensions; index++ ) {
            inputLayer[index] = (((float)(randomNumberGenerator.nextInt()) / (float)(Integer.MAX_VALUE)) - 0.5f)
                / vectorDimensions;
        }
        if ( hierarchicalSoftmax ) {
            outputLayer = new float [vocabulary.getNrWords() * vectorDimensions];
        }
        if ( negativeSamples > 0 ) {
            if (usePosition) {
                outputLayerNegativeSamples = new float [vocabulary.getNrWords() * vectorDimensions * windowSize * 2];
            } else {
                outputLayerNegativeSamples = new float [vocabulary.getNrWords() * vectorDimensions];
            }
        }
        vocabulary.generateCodes();
    }

    public synchronized float getValueInputLayer(int item) {
        return inputLayer[item];
    }

    public synchronized void setValueInputLayer(int item, float value) {
        inputLayer[item] = value;
    }

    public synchronized void incrementValueInputLayer(int item, float increment) {
        inputLayer[item] += increment;
    }

    public synchronized float getValueOutputLayer(int item) {
        return outputLayer[item];
    }

    public synchronized  void setValueOutputLayer(int item, float value) {
        outputLayer[item] = value;
    }

    public synchronized void incrementValueOutputLayer(int item, float increment) {
        outputLayer[item] += increment;
    }

    public synchronized float getValueOutputLayerNegativeSamples(int item) {
        return outputLayerNegativeSamples[item];
    }

    public synchronized void setValueOutputNegativeSamples(int item, float value) {
        outputLayerNegativeSamples[item] = value;
    }

    public synchronized void incrementValueOutputNegativeSamples(int item, float increment) {
        outputLayerNegativeSamples[item] += increment;
    }

    public synchronized long getGlobalWordCount() {
        return globalWordCount;
    }

    public synchronized void incrementGlobalWordCount(long increment) {
        globalWordCount += increment;
    }

    public synchronized float getCurrentAlpha() {
        return currentAlpha;
    }

    public synchronized void setCurrentAlpha(float alpha) {
        currentAlpha = alpha;
    }
}
