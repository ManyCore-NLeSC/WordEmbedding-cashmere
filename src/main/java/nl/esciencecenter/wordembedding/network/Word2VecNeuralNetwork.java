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
    private Boolean CBOW;
    private Boolean hierarchicalSoftmax;
    private Boolean usePosition;
    private Boolean debug = false;
    private Integer negativeSamples;
    private Integer vectorDimensions;
    private Integer windowSize;
    private Integer updateInterval = 10000;
    private Integer globalWordCount = 0;
    private Integer currentWordCount = 0;
    private Integer previousWordCount = 0;
    private Float alpha;
    private Float currentAlpha;
    private Float samplingFactor = 0.0f;
    private float [] exponentialTable;
    private float [] inputLayer;
    private float [] hiddenLayer0;
    private float [] hiddenError0;
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

    // The code of this method is a straightforward translation of Google's C code
    // TODO: check if it could be written in a better way
    public void trainModel(Vocabulary vocabulary, BufferedReader fileReader) throws IOException {
        Integer sentencePosition = 0;
        String line;
        Random randomNumberGenerator = new Random();

        hiddenLayer0 = new float [vectorDimensions];
        hiddenError0 = new float [vectorDimensions];
        // Training loop
        while ( (line = fileReader.readLine()) != null ) {
            ArrayList<String> sentence = new ArrayList<>();

            if ( (currentWordCount - previousWordCount) > updateInterval ) {
                globalWordCount += currentWordCount - previousWordCount;
                previousWordCount = currentWordCount;
                if ( debug ) {
                    System.out.format("Alpha: %.10f\t\tProgress: %.2f%%%n", currentAlpha,
                            (globalWordCount / (float)(vocabulary.getOccurrences() + 1)) * 100);
                }
                currentAlpha = alpha * (1 - (globalWordCount / (float)(vocabulary.getOccurrences() + 1)));
                if ( currentAlpha < alpha * 0.0001f ) {
                    currentAlpha = alpha * 0.0001f;
                }
            }
            if ( sentence.size() == 0 ) {
                while ( !line.isEmpty() ) {
                    String word = ReadWord.readWord(line, false);

                    if ( word == null ) {
                        line = line.trim();
                        continue;
                    } else {
                        line = line.substring(word.length());
                        line = line.trim();
                    }
                    if ( vocabulary.getWord(word) == null ) {
                        continue;
                    }
                    currentWordCount++;
                    if ( samplingFactor > 0 ) {
                        Float sample = (float)((Math.sqrt(vocabulary.getWord(word).getOccurrences()
                                / (samplingFactor * vocabulary.getNrWords())) + 1)
                                * (samplingFactor * vocabulary.getNrWords())
                                / vocabulary.getWord(word).getOccurrences());

                        if ( sample < randomNumberGenerator.nextFloat() ) {
                            continue;
                        }
                    }
                    sentence.add(word);
                }
                sentencePosition = 0;
            }
            if ( sentence.size() == 0 ) {
                // If there are no words in the sentence, read another line.
                continue;
            }
            String word = sentence.get(sentencePosition);
            for ( int neuronIndex = 0; neuronIndex < hiddenLayer0.length; neuronIndex++ ) {
                hiddenLayer0[neuronIndex] = 0.0f;
                hiddenError0[neuronIndex] = 0.0f;
            }
            Integer randomStartingWord = randomNumberGenerator.nextInt() % windowSize;
            if ( CBOW ) {
                CBOW(vocabulary, sentence, word, sentencePosition, randomStartingWord);
            } else {
                skipGram(vocabulary, sentence, word, sentencePosition, randomStartingWord);
            }
            sentencePosition++;
            if ( sentencePosition >= sentence.size() ) {
                sentence.clear();
            }
        }
    }

    // The code of this method is a straightforward translation of Google's C code
    // TODO: check if it could be written in a better way
    private void CBOW(Vocabulary vocabulary, ArrayList<String> sentence, String word, Integer sentencePosition,
                      Integer randomStartingWord) {
        for ( int wordIndex = randomStartingWord; wordIndex < (windowSize * 2 + 1) - randomStartingWord; wordIndex++ ) {
            if ( wordIndex != windowSize ) {
                Integer lastWordIndex = sentencePosition - windowSize + wordIndex;
                if ( lastWordIndex < 0 || lastWordIndex >= sentence.size() ) {
                    continue;
                }
                lastWordIndex = vocabulary.getWord(sentence.get(lastWordIndex)).getSortedIndex();
                if ( lastWordIndex == -1 ) {
                    continue;
                }
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    hiddenLayer0[neuronIndex] = hiddenLayer0[neuronIndex]
                            + inputLayer[(lastWordIndex * vectorDimensions) + neuronIndex];
                }
            }
        }
        if ( hierarchicalSoftmax ) {
            for ( int symbolIndex = 0; symbolIndex < vocabulary.getWord(word).getCodeLength(); symbolIndex++ ) {
                Float exponential = 0.0f;
                Float gradient;
                Integer relatedWordIndex = vocabulary.getWord(word).getPoint(symbolIndex) * vectorDimensions;

                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    exponential += hiddenLayer0[neuronIndex] * outputLayer[relatedWordIndex + neuronIndex];
                }
                if ( (exponential <= -MAX_EXP) || exponential >= MAX_EXP ) {
                    continue;
                }
                exponential = exponentialTable[(int)((exponential + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2))];
                gradient = (1 - vocabulary.getWord(word).getCode(symbolIndex) - exponential) * currentAlpha;
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    hiddenError0[neuronIndex] = hiddenError0[neuronIndex]
                            + (gradient * outputLayer[relatedWordIndex + neuronIndex]);
                    outputLayer[relatedWordIndex + neuronIndex] = outputLayer[relatedWordIndex + neuronIndex]
                            + (gradient * hiddenLayer0[neuronIndex]);
                }
            }
        }
        if ( negativeSamples > 0 ) {
            Integer target;
            Integer label;
            Integer relatedWordIndex;
            Float exponential;
            Float gradient;
            Random randomNumberGenerator = new Random();

            for ( int sample = 0; sample < negativeSamples + 1; sample++ ) {
                if ( sample == 0 ) {
                    target = vocabulary.getWord(word).getSortedIndex();
                    label = 1;
                } else {
                    target = randomNumberGenerator.nextInt(vocabulary.getNrWords());
                    if ( target == 0 ) {
                        target = randomNumberGenerator.nextInt(vocabulary.getNrWords()) + 1;
                    } else if ( target.equals(vocabulary.getWord(word).getSortedIndex()) ) {
                        continue;
                    }
                    label = 0;
                }
                exponential = 0.0f;
                relatedWordIndex = target * vectorDimensions;
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    exponential += hiddenLayer0[neuronIndex]
                            * outputLayerNegativeSamples[relatedWordIndex + neuronIndex];
                }
                if ( exponential > MAX_EXP ) {
                    gradient = (label - 1) * currentAlpha;
                } else if ( exponential < -MAX_EXP ) {
                    gradient = label * currentAlpha;
                } else {
                    gradient = (label
                            - exponentialTable[(int)((exponential + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2))])
                            * currentAlpha;
                }
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    hiddenError0[neuronIndex] = hiddenError0[neuronIndex]
                            + (gradient * outputLayerNegativeSamples[relatedWordIndex + neuronIndex]);
                    outputLayerNegativeSamples[relatedWordIndex + neuronIndex] =
                            outputLayerNegativeSamples[relatedWordIndex + neuronIndex]
                                    + (gradient * hiddenLayer0[neuronIndex]);
                }
            }
        }
        for ( int wordIndex = randomStartingWord; wordIndex < (windowSize * 2) + 1; wordIndex++ ) {
            if ( wordIndex != windowSize ) {
                Integer lastWordIndex = sentencePosition - windowSize + wordIndex;

                if ( lastWordIndex < 0 || lastWordIndex >= sentence.size() ) {
                    continue;
                }
                lastWordIndex = vocabulary.getWord(sentence.get(lastWordIndex)).getSortedIndex();
                if ( lastWordIndex == -1 ) {
                    continue;
                }
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    inputLayer[(lastWordIndex * vectorDimensions) + neuronIndex] =
                            inputLayer[(lastWordIndex * vectorDimensions) + neuronIndex]
                                    + hiddenError0[neuronIndex];
                }
            }
        }
    }

    // The code of this method is a straightforward translation of Google's C code
    // TODO: check if it could be written in a better way
    private void skipGram(Vocabulary vocabulary, ArrayList<String> sentence, String word, Integer sentencePosition,
                          Integer randomStartingWord) {
        Integer lastWordIndex;
        Integer relatedWordIndexOne;
        Integer relatedWordIndexTwo;
        Float exponential;
        Float gradient;

        for ( int wordIndex = randomStartingWord; wordIndex < (windowSize * 2) - 1; wordIndex++ ) {
            if ( wordIndex != windowSize ) {
                lastWordIndex = sentencePosition - windowSize + wordIndex;
                if ( lastWordIndex < 0 || lastWordIndex >= sentence.size() ) {
                    continue;
                }
                lastWordIndex = vocabulary.getWord(sentence.get(lastWordIndex)).getSortedIndex();
                if ( lastWordIndex == -1 ) {
                    continue;
                }
                relatedWordIndexOne = lastWordIndex * vectorDimensions;
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    hiddenError0[neuronIndex] = 0.0f;
                }
                if ( hierarchicalSoftmax ) {
                    for ( int symbolIndex = 0; symbolIndex < vocabulary.getWord(word).getCodeLength(); symbolIndex++ ) {
                        exponential = 0.0f;
                        relatedWordIndexTwo = vocabulary.getWord(word).getPoint(symbolIndex) * vectorDimensions;
                        for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                            exponential += inputLayer[relatedWordIndexOne + neuronIndex]
                                    * outputLayer[relatedWordIndexTwo + neuronIndex];
                        }
                        if ( exponential <= -MAX_EXP || exponential >= MAX_EXP ) {
                            continue;
                        }
                        exponential = exponentialTable[(int)((exponential + MAX_EXP)
                                * (EXP_TABLE_SIZE / MAX_EXP / 2))];
                        gradient = (1 - vocabulary.getWord(word).getCode(symbolIndex) - exponential)
                                * currentAlpha;
                        for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                            hiddenError0[neuronIndex] = hiddenError0[neuronIndex]
                                    + (gradient * outputLayer[relatedWordIndexTwo + neuronIndex]);
                            outputLayer[relatedWordIndexTwo + neuronIndex] =
                                    outputLayer[relatedWordIndexTwo + neuronIndex]
                                            + (gradient * inputLayer[relatedWordIndexOne + neuronIndex]);
                        }
                    }
                }
                if ( negativeSamples > 0 ) {
                    Integer target;
                    Integer label;
                    Random randomNumberGenerator = new Random();

                    for ( int sample = 0; sample < negativeSamples + 1; sample++ ) {
                        if ( sample == 0 ) {
                            target = vocabulary.getWord(word).getSortedIndex();
                            label = 1;
                        } else {
                            target = randomNumberGenerator.nextInt(vocabulary.getNrWords());
                            if ( target == 0 ) {
                                target = randomNumberGenerator.nextInt(vocabulary.getNrWords()) + 1;
                            } else if ( target.equals(vocabulary.getWord(word).getSortedIndex()) ) {
                                continue;
                            }
                            label = 0;
                        }
                        if ( usePosition ) {
                            relatedWordIndexTwo = (windowSize * 2 * target) * vectorDimensions;
                            if ( wordIndex > windowSize ) {
                                relatedWordIndexTwo += wordIndex - 1;
                            } else {
                                relatedWordIndexTwo += wordIndex;
                            }
                        } else {
                            relatedWordIndexTwo = target * vectorDimensions;
                        }
                        exponential = 0.0f;
                        for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                            exponential += inputLayer[relatedWordIndexOne + neuronIndex]
                                    * outputLayerNegativeSamples[relatedWordIndexTwo + neuronIndex];
                        }
                        if ( exponential > MAX_EXP ) {
                            gradient = (label - 1) * currentAlpha;
                        } else if ( exponential < -MAX_EXP ) {
                            gradient = label * currentAlpha;
                        } else {
                            gradient = (label
                                    - exponentialTable[(int)((exponential + MAX_EXP)
                                    * (EXP_TABLE_SIZE / MAX_EXP / 2))]) * currentAlpha;
                        }
                        for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                            hiddenError0[neuronIndex] = hiddenError0[neuronIndex] + (gradient
                                    * outputLayerNegativeSamples[relatedWordIndexTwo + neuronIndex]);
                            outputLayerNegativeSamples[relatedWordIndexTwo + neuronIndex] =
                                    outputLayerNegativeSamples[relatedWordIndexTwo + neuronIndex]
                                            + (gradient * inputLayer[relatedWordIndexOne + neuronIndex]);
                        }
                    }
                }
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    inputLayer[relatedWordIndexOne + neuronIndex] =
                            inputLayer[relatedWordIndexOne + neuronIndex] + hiddenError0[neuronIndex];
                }
            }
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
