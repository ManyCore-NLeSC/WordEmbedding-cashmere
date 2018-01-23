package nl.esciencecenter.wordembedding.network;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.utilities.ReadWord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Word2VecNeuralNetwork {
    private final Integer MAX_EXP = 6;
    private final Integer EXP_TABLE_SIZE = 1000;
    private final Boolean CBOW;
    private final Boolean hierarchicalSoftmax;
    private final Boolean usePosition;
    private Boolean debug = false;
    private final Integer negativeSamples;
    private final Integer vectorDimensions;
    private final Integer windowSize;
    private Integer threads = 1;
    private volatile Integer globalWordCount = 0;
    private final Float alpha;
    private volatile Float currentAlpha;
    private Float samplingFactor = 0.0f;
    private volatile float [] exponentialTable;
    private volatile float [] inputLayer;
    private volatile float [] hiddenLayer0;
    private volatile float [] hiddenError0;
    private volatile float [] outputLayer;
    private volatile float [] outputLayerNegativeSamples;

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

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public Boolean getDebug() {
        return debug;
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

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getThreads() {
        return threads;
    }

    public void initialize(Vocabulary vocabulary) {
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

    public void initializeExponentialTable() {
        exponentialTable = new float [EXP_TABLE_SIZE + 1];
        for ( int x = 0; x < EXP_TABLE_SIZE; x++ ) {
            exponentialTable[x] = (float)(Math.exp((((x / (float)(EXP_TABLE_SIZE)) * 2) - 1) * MAX_EXP));
            exponentialTable[x] = exponentialTable[x] / (exponentialTable[x] + 1);
        }
    }

    public void trainModel(Vocabulary vocabulary, BufferedReader fileReader) {

        for ( int thread = 0; thread < threads; thread++ ) {
            Word2VecNeuralNetworkWorker worker = new Word2VecNeuralNetworkWorker(globalWordCount, currentAlpha,
                    exponentialTable, inputLayer, outputLayer, outputLayerNegativeSamples, vocabulary, fileReader);
            worker.setCBOW(CBOW);
            worker.setDebug(debug);
            worker.setHierarchicalSoftmax(hierarchicalSoftmax);
            worker.setUsePosition(usePosition);
            worker.setMAX_EXP(MAX_EXP);
            worker.setEXP_TABLE_SIZE(EXP_TABLE_SIZE);
            worker.setVectorDimensions(vectorDimensions);
            worker.setWindowSize(windowSize);
            worker.setNegativeSamples(negativeSamples);
            worker.setAlpha(alpha);
            worker.setSamplingFactor(samplingFactor);
        }
    }

    public void saveWordVectors(Vocabulary vocabulary, BufferedWriter fileWriter) throws IOException {
        fileWriter.write(vocabulary.getNrWords() + " " + vectorDimensions);
        fileWriter.newLine();
        for ( Word word : vocabulary.getWords() ) {
            fileWriter.write(word.getWord() + " ");
            for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                fileWriter.write(String.format("%.6f ",
                        inputLayer[(word.getSortedIndex() * vectorDimensions) + neuronIndex]));
            }
            fileWriter.newLine();
        }
    }

    public void saveContextVectors(Vocabulary vocabulary, BufferedWriter fileWriter) throws IOException {
        fileWriter.write(vocabulary.getNrWords() + " " + vectorDimensions);
        fileWriter.newLine();
        for ( Word word : vocabulary.getWords() ) {
            fileWriter.write(word.getWord() + " ");
            for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                fileWriter.write(String.format("%.6f ", outputLayerNegativeSamples[(word.getSortedIndex() * vectorDimensions)
                        + neuronIndex]));
            }
            fileWriter.newLine();
        }
    }

    public void saveClasses(Vocabulary vocabulary, BufferedWriter fileWriter, Integer nrClasses) throws IOException {
        Integer nrIterations = 10;
        ArrayList<Integer> classMapping = new ArrayList<>(vocabulary.getNrWords());
        ArrayList<Integer> classCounter = new ArrayList<>(nrClasses);
        ArrayList<Float> classVector = new ArrayList<>(nrClasses * vectorDimensions);

        for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
            classMapping.add(wordIndex % nrClasses);
        }
        for ( int iteration = 0; iteration < nrIterations; iteration++ ) {
            for ( int neuronIndex = 0; neuronIndex < nrClasses * vectorDimensions; neuronIndex++ ) {
                classVector.add(0.0f);
            }
            for ( int classIndex = 0; classIndex < nrClasses; classIndex++ ) {
                classCounter.add(1);
            }
            for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    classVector.set((classMapping.get(wordIndex) * vectorDimensions) + neuronIndex,
                            classVector.get((classMapping.get(wordIndex) * vectorDimensions) + neuronIndex)
                                    + inputLayer[(wordIndex * vectorDimensions) + neuronIndex]);
                }
                classCounter.set(classMapping.get(wordIndex), classCounter.get(classMapping.get(wordIndex)) + 1);
            }
            for ( int classIndex = 0; classIndex < nrClasses; classIndex++ ) {
                Float distance = 0.0f;

                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    classVector.set((classIndex * vectorDimensions) + neuronIndex,
                            classVector.get((classIndex * vectorDimensions) + neuronIndex)
                                    / classCounter.get(classIndex));
                    distance += classVector.get((classIndex * vectorDimensions) + neuronIndex)
                            * classVector.get((classIndex * vectorDimensions) + neuronIndex);
                }
                distance = (float)(Math.sqrt(distance));
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    classVector.set((classIndex * vectorDimensions) + neuronIndex,
                            classVector.get((classIndex * vectorDimensions) + neuronIndex) / distance);
                }
            }
            for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
                Integer classID = 0;
                Float distance = -10.0f;

                for ( int classIndex = 0; classIndex < nrClasses; classIndex++ ) {
                    Float position = 0.0f;

                    for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                        position += classVector.get((classIndex * vectorDimensions) + neuronIndex)
                        * inputLayer[(wordIndex * vectorDimensions) + neuronIndex];
                    }
                    if ( position > distance ) {
                        distance = position;
                        classID = classIndex;
                    }
                }
                classMapping.set(wordIndex, classID);
            }
        }
        for ( Word word : vocabulary.getWords() ) {
            fileWriter.write( word.getWord() + " " + classMapping.get(word.getSortedIndex()));
            fileWriter.newLine();
        }
    }
}
