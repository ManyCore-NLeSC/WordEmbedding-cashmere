package nl.esciencecenter.wordembedding.data;

import java.util.Random;

public class Word2VecNeuralNetwork {
    private final Boolean CBOW;
    private final Boolean hierarchicalSoftmax;
    private final Boolean usePosition;
    private final Integer negativeSamples;
    private final Integer vectorDimensions;
    private final Integer windowSize;
    private final Float alpha;
    private Float samplingFactor = 0.0f;
    // Members that can be modified concurrently
    private Integer globalWordCount = 0;
    private Float currentAlpha;
    private float [] inputLayer;
    private float [] outputLayer;
    private float [] outputLayerNegativeSamples;

    public Word2VecNeuralNetwork(Boolean CBOW, Boolean hierarchicalSoftmax, Boolean usePosition, Integer negativeSamples,
                                 Integer vectorDimensions, Integer windowSize, Float alpha) {
        this.CBOW = CBOW;
        this.hierarchicalSoftmax = hierarchicalSoftmax;
        this.usePosition = usePosition;
        this.negativeSamples = negativeSamples;
        this.vectorDimensions = vectorDimensions;
        this.windowSize = windowSize;
        this.alpha = alpha;
        this.currentAlpha = alpha;
    }

    public Boolean getSkipGram() {
        return !CBOW;
    }

    public Boolean getCBOW() {
        return CBOW;
    }

    public Boolean getHierarchicalSoftmax() {
        return hierarchicalSoftmax;
    }

    public Boolean getUsePosition() {
        return usePosition;
    }

    public Integer getNegativeSamples() {
        return negativeSamples;
    }

    public Integer getVectorDimensions() {
        return vectorDimensions;
    }

    public void setSamplingFactor(Float samplingFactor) {
        this.samplingFactor = samplingFactor;
    }

    public Float getSamplingFactor() {
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

    public Float getAlpha() {
        return alpha;
    }

    public Integer getWindowSize() {
        return windowSize;
    }

    //
    // Synchronized methods
    //
    public synchronized void initialize(Vocabulary vocabulary) {
        Random randomNumberGenerator = new Random();

        inputLayer = new float [vocabulary.getNrWords() * vectorDimensions];
        for ( int index = 0; index < vocabulary.getNrWords() * vectorDimensions; index++ ) {
            inputLayer[index] = ((randomNumberGenerator.nextInt() / (float)(Integer.MAX_VALUE)) - 0.5f)
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

    public synchronized float getValueInputLayer(Integer item) {
        return inputLayer[item];
    }

    public synchronized void setValueInputLayer(Integer item, float value) {
        inputLayer[item] = value;
    }

    public synchronized void incrementValueInputLayer(Integer item, float increment) {
        inputLayer[item] += increment;
    }

    public synchronized float getValueOutputLayer(Integer item) {
        return outputLayer[item];
    }

    public synchronized  void setValueOutputLayer(Integer item, float value) {
        outputLayer[item] = value;
    }

    public synchronized void incrementValueOutputLayer(Integer item, float increment) {
        outputLayer[item] += increment;
    }

    public synchronized float getValueOutputLayerNegativeSamples(Integer item) {
        return outputLayerNegativeSamples[item];
    }

    public synchronized void setValueOutputNegativeSamples(Integer item, float value) {
        outputLayerNegativeSamples[item] = value;
    }

    public synchronized void incrementValueOutputNegativeSamples(Integer item, float increment) {
        outputLayerNegativeSamples[item] += increment;
    }

    public synchronized Integer getGlobalWordCount() {
        return globalWordCount;
    }

    public synchronized void incrementGlobalWordCount(Integer increment) {
        globalWordCount += increment;
    }

    public synchronized Float getCurrentAlpha() {
        return currentAlpha;
    }

    public synchronized void setCurrentAlpha(Float alpha) {
        currentAlpha = alpha;
    }
}
