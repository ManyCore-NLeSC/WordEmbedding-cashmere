package nl.esciencecenter.word2vec.network;

import nl.esciencecenter.word2vec.data.Vocabulary;
import nl.esciencecenter.word2vec.data.Word;
import nl.esciencecenter.word2vec.utilities.ReadWord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class NeuralNetwork {
    private final Integer MAX_EXP = 6;
    private final Integer EXP_TABLE_SIZE = 1000;
    private Boolean CBOW = false;
    private Boolean hierarchicalSoftmax = false;
    private Boolean usePosition = false;
    private Boolean debug = false;
    private Integer negativeSamples = 0;
    private Integer vectorDimensions = 0;
    private Integer windowSize = 0;
    private Integer unigramTableSize = 100000000;
    private Integer updateInterval = 10000;
    private Integer globalWordCount = 0;
    private Integer currentWordCount = 0;
    private Integer previousWordCount = 0;
    private Float alpha = 0.025f;
    private Float currentAlpha = 0.0f;
    private Float samplingFactor = 0.0f;
    private ArrayList<Integer> unigramTable;
    private ArrayList<Float> exponentialTable;
    private ArrayList<Float> inputLayer;
    private ArrayList<Float> hiddenLayer0;
    private ArrayList<Float> hiddenError0;
    private ArrayList<Float> outputLayer;
    private ArrayList<Float> outputLayerNegativeSamples;

    public NeuralNetwork(Boolean CBOW, Boolean hierarchicalSoftmax, Boolean usePosition, Integer negativeSamples,
                         Integer vectorDimensions, Integer windowSize, Float alpha) {
        this.CBOW = CBOW;
        this.hierarchicalSoftmax = hierarchicalSoftmax;
        this.usePosition = usePosition;
        this.negativeSamples = negativeSamples;
        this.vectorDimensions = vectorDimensions;
        this.windowSize = windowSize;
        this.alpha = alpha;
        currentAlpha = alpha;
        unigramTable = new ArrayList<>();
        exponentialTable = new ArrayList<>(EXP_TABLE_SIZE + 1);
        inputLayer = new ArrayList<>();
        hiddenLayer0 = new ArrayList<>();
        hiddenError0 = new ArrayList<>();
        outputLayer = new ArrayList<>();
        outputLayerNegativeSamples = new ArrayList<>();
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

        inputLayer.ensureCapacity(vocabulary.getNrWords() * vectorDimensions);
        for ( int index = 0; index < vocabulary.getNrWords() * vectorDimensions; index++ ) {
            inputLayer.add(((randomNumberGenerator.nextInt() / (float)(Integer.MAX_VALUE)) - 0.5f) / vectorDimensions);
        }
        if ( hierarchicalSoftmax ) {
            outputLayer.ensureCapacity(vocabulary.getNrWords() * vectorDimensions);
            for ( int index = 0; index < vocabulary.getNrWords() * vectorDimensions; index++ ) {
                outputLayer.add(0.0f);
            }
        }
        if ( negativeSamples > 0 ) {
            if (usePosition) {
                outputLayerNegativeSamples.ensureCapacity(vocabulary.getNrWords() * vectorDimensions
                        * windowSize * 2);
                for ( int index = 0; index < vocabulary.getNrWords() * vectorDimensions * windowSize * 2; index++ ) {
                    outputLayerNegativeSamples.add(0.0f);
                }
            } else {
                outputLayerNegativeSamples.ensureCapacity(vocabulary.getNrWords() * vectorDimensions);
                for ( int index = 0; index < vocabulary.getNrWords() * vectorDimensions; index++ ) {
                    outputLayerNegativeSamples.add(0.0f);
                }
            }
        }
        vocabulary.generateCodes();
    }

    public void initializeExponentialTable() {
        exponentialTable.ensureCapacity(EXP_TABLE_SIZE + 1);
        for ( int x = 0; x < EXP_TABLE_SIZE; x++ ) {
            exponentialTable.add((float)(Math.exp((((x / (float)(EXP_TABLE_SIZE)) * 2) - 1) * MAX_EXP)));
            exponentialTable.set(x, exponentialTable.get(x) / (exponentialTable.get(x) + 1));
        }
    }

    // The code of this method is a straightforward translation of Google's C code
    // TODO: check if it could be written in a better way
    public void initializeUnigramTable(Vocabulary vocabulary) {
        Integer wordsPower = 0;
        Integer wordIndex;
        Float exponent = 0.75f;
        Float d1;

        unigramTable.ensureCapacity(unigramTableSize);
        for ( int index = 0; index < unigramTableSize; index++ ) {
            unigramTable.add(0);
        }
        for ( Word word : vocabulary.getWords() ) {
            wordsPower += ((int) Math.pow(word.getOccurrences(), exponent));
        }
        wordIndex = 0;
        d1 = (float)(Math.pow(vocabulary.getWord(vocabulary.getSortedWords().get(wordIndex)).getOccurrences(), exponent)
                / wordsPower);
        for ( int tableIndex = 0; tableIndex < unigramTable.size(); tableIndex++ ) {
            unigramTable.set(tableIndex, wordIndex);
            if ( tableIndex / (float)(unigramTable.size()) > d1 ) {
                wordIndex++;
                d1 += (float)(Math.pow(vocabulary.getWord(vocabulary.getSortedWords().get(wordIndex)).getOccurrences(),
                        exponent) / wordsPower);
            }
            if ( wordIndex >= vocabulary.getNrWords() ) {
                wordIndex = vocabulary.getNrWords() -1;
            }
        }
    }

    // The code of this method is a straightforward translation of Google's C code
    // TODO: check if it could be written in a better way
    public void trainModel(Vocabulary vocabulary, BufferedReader fileReader) throws IOException {
        Integer sentencePosition = 0;
        String line;
        Random randomNumberGenerator = new Random();

        hiddenLayer0.ensureCapacity(vectorDimensions);
        hiddenError0.ensureCapacity(vectorDimensions);
        for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
            hiddenLayer0.add(0.0f);
            hiddenError0.add(0.0f);
        }
        // Training loop
        while ( (line = fileReader.readLine()) != null ) {
            ArrayList<String> sentence = new ArrayList<>();

            if ( (currentWordCount - previousWordCount) > updateInterval ) {
                globalWordCount += currentWordCount - previousWordCount;
                previousWordCount = currentWordCount;
                if ( debug ) {
                    System.out.println("Alpha: " + currentAlpha
                            + " Progress: " + ((globalWordCount / (float)(vocabulary.getNrWords() + 1)) * 100));
                }
                currentAlpha = alpha * (1 - (globalWordCount / (float)(vocabulary.getNrWords() + 1)));
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
            for ( int neuronIndex = 0; neuronIndex < hiddenLayer0.size(); neuronIndex++ ) {
                hiddenLayer0.set(neuronIndex, 0.0f);
                hiddenError0.set(neuronIndex, 0.0f);
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
                lastWordIndex = vocabulary.getSortedIndex(sentence.get(lastWordIndex));
                if ( lastWordIndex == -1 ) {
                    continue;
                }
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    hiddenLayer0.set(neuronIndex,
                            hiddenLayer0.get(neuronIndex)
                                    + inputLayer.get((lastWordIndex * vectorDimensions) + neuronIndex));
                }
            }
        }
        if ( hierarchicalSoftmax ) {
            for ( int symbolIndex = 0; symbolIndex < vocabulary.getWord(word).getCodeLength(); symbolIndex++ ) {
                Float exponential = 0.0f;
                Float gradient;
                Integer relatedWordIndex = vocabulary.getWord(word).getPoints()[symbolIndex] * vectorDimensions;

                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    exponential += hiddenLayer0.get(neuronIndex) * outputLayer.get(relatedWordIndex + neuronIndex);
                }
                if ( (exponential <= -MAX_EXP) || exponential >= MAX_EXP ) {
                    continue;
                }
                exponential = exponentialTable.get((int)((exponential + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2)));
                gradient = (1 - vocabulary.getWord(word).getCodes()[symbolIndex] - exponential) * currentAlpha;
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    hiddenError0.set(neuronIndex,
                            hiddenError0.get(neuronIndex)
                                    + (gradient * outputLayer.get(relatedWordIndex + neuronIndex)));
                    outputLayer.set(relatedWordIndex + neuronIndex,
                            outputLayer.get(relatedWordIndex + neuronIndex)
                                    + (gradient * hiddenLayer0.get(neuronIndex)));
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
                    target = vocabulary.getSortedIndex(word);
                    label = 1;
                } else {
                    target = randomNumberGenerator.nextInt(unigramTable.size());
                    if ( target == 0 ) {
                        target = randomNumberGenerator.nextInt(vocabulary.getNrWords()) + 1;
                    } else if ( target.equals(vocabulary.getSortedIndex(word)) ) {
                        continue;
                    }
                    label = 0;
                }
                exponential = 0.0f;
                relatedWordIndex = target * vectorDimensions;
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    exponential += hiddenLayer0.get(neuronIndex)
                            * outputLayerNegativeSamples.get(relatedWordIndex + neuronIndex);
                }
                if ( exponential > MAX_EXP ) {
                    gradient = (label - 1) * currentAlpha;
                } else if ( exponential < -MAX_EXP ) {
                    gradient = label * currentAlpha;
                } else {
                    gradient = (label
                            - exponentialTable.get((int)((exponential + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2))))
                            * currentAlpha;
                }
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    hiddenError0.set(neuronIndex,
                            hiddenError0.get(neuronIndex)
                                    + (gradient * outputLayerNegativeSamples.get(relatedWordIndex + neuronIndex)));
                    outputLayerNegativeSamples.set(relatedWordIndex + neuronIndex,
                            outputLayerNegativeSamples.get(relatedWordIndex + neuronIndex)
                                    + (gradient * hiddenLayer0.get(neuronIndex)));
                }
            }
        }
        for ( int wordIndex = randomStartingWord; wordIndex < (windowSize * 2) + 1; wordIndex++ ) {
            if ( wordIndex != windowSize ) {
                Integer lastWordIndex = sentencePosition - windowSize + wordIndex;

                if ( lastWordIndex < 0 || lastWordIndex >= sentence.size() ) {
                    continue;
                }
                lastWordIndex = vocabulary.getSortedIndex(sentence.get(lastWordIndex));
                if ( lastWordIndex == -1 ) {
                    continue;
                }
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    inputLayer.set((lastWordIndex * vectorDimensions) + neuronIndex,
                            inputLayer.get((lastWordIndex * vectorDimensions) + neuronIndex)
                                    + hiddenError0.get(neuronIndex));
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
                lastWordIndex = vocabulary.getSortedIndex(sentence.get(lastWordIndex));
                if ( lastWordIndex == -1 ) {
                    continue;
                }
                relatedWordIndexOne = lastWordIndex * vectorDimensions;
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    hiddenError0.set(neuronIndex, 0.0f);
                }
                if ( hierarchicalSoftmax ) {
                    for ( int symbolIndex = 0; symbolIndex < vocabulary.getWord(word).getCodeLength(); symbolIndex++ ) {
                        exponential = 0.0f;
                        relatedWordIndexTwo = vocabulary.getWord(word).getPoints()[symbolIndex] * vectorDimensions;
                        for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                            exponential += inputLayer.get(relatedWordIndexOne + neuronIndex)
                                    * outputLayer.get(relatedWordIndexTwo + neuronIndex);
                        }
                        if ( exponential <= -MAX_EXP || exponential >= MAX_EXP ) {
                            continue;
                        }
                        exponential = exponentialTable.get((int)((exponential + MAX_EXP)
                                * (EXP_TABLE_SIZE / MAX_EXP / 2)));
                        gradient = (1 - vocabulary.getWord(word).getCodes()[symbolIndex] - exponential)
                                * currentAlpha;
                        for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                            hiddenError0.set(neuronIndex, hiddenError0.get(neuronIndex)
                                    + (gradient * outputLayer.get(relatedWordIndexTwo + neuronIndex)));
                            outputLayer.set(relatedWordIndexTwo + neuronIndex,
                                    outputLayer.get(relatedWordIndexTwo + neuronIndex)
                                            + (gradient * inputLayer.get(relatedWordIndexOne + neuronIndex)));
                        }
                    }
                }
                if ( negativeSamples > 0 ) {
                    Integer target;
                    Integer label;
                    Random randomNumberGenerator = new Random();

                    for ( int sample = 0; sample < negativeSamples + 1; sample++ ) {
                        if ( sample == 0 ) {
                            target = vocabulary.getSortedIndex(word);
                            label = 1;
                        } else {
                            target = randomNumberGenerator.nextInt(unigramTable.size());
                            if ( target == 0 ) {
                                target = randomNumberGenerator.nextInt(vocabulary.getNrWords()) + 1;
                            } else if ( target.equals(vocabulary.getSortedIndex(word)) ) {
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
                            exponential += inputLayer.get(relatedWordIndexOne + neuronIndex)
                                    * outputLayerNegativeSamples.get(relatedWordIndexTwo + neuronIndex);
                        }
                        if ( exponential > MAX_EXP ) {
                            gradient = (label - 1) * currentAlpha;
                        } else if ( exponential < -MAX_EXP ) {
                            gradient = label * currentAlpha;
                        } else {
                            gradient = (label
                                    - exponentialTable.get((int)((exponential + MAX_EXP)
                                    * (EXP_TABLE_SIZE / MAX_EXP / 2)))) * currentAlpha;
                        }
                        for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                            hiddenError0.set(neuronIndex,
                                    hiddenError0.get(neuronIndex)
                                            + (gradient
                                            * outputLayerNegativeSamples.get(relatedWordIndexTwo + neuronIndex)));
                            outputLayerNegativeSamples.set(relatedWordIndexTwo + neuronIndex,
                                    outputLayerNegativeSamples.get(relatedWordIndexTwo + neuronIndex)
                                            + (gradient * inputLayer.get(relatedWordIndexOne + neuronIndex)));
                        }
                    }
                }
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    inputLayer.set(relatedWordIndexOne + neuronIndex,
                            inputLayer.get(relatedWordIndexOne + neuronIndex) + hiddenError0.get(neuronIndex));
                }
            }
        }
    }

    public void saveWordVectors(Vocabulary vocabulary, BufferedWriter fileWriter) throws IOException {
        fileWriter.write(vocabulary.getNrWords() + " " + vectorDimensions);
        fileWriter.newLine();
        for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
            fileWriter.write(vocabulary.getSortedWords().get(wordIndex) + " ");
            for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                fileWriter.write(inputLayer.get((wordIndex * vectorDimensions) + neuronIndex) + " ");
            }
            fileWriter.newLine();
        }
    }

    public void saveContextVectors(Vocabulary vocabulary, BufferedWriter fileWriter) throws IOException {
        fileWriter.write(vocabulary.getNrWords() + " " + vectorDimensions);
        fileWriter.newLine();
        for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
            fileWriter.write(vocabulary.getSortedWords().get(wordIndex) + " ");
            for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                fileWriter.write(outputLayerNegativeSamples.get((wordIndex * vectorDimensions) + neuronIndex) + " ");
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
                                    + inputLayer.get((wordIndex * vectorDimensions) + neuronIndex));
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
                        * inputLayer.get((wordIndex * vectorDimensions) + neuronIndex);
                    }
                    if ( position > distance ) {
                        distance = position;
                        classID = classIndex;
                    }
                }
                classMapping.set(wordIndex, classID);
            }
        }
        for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
            fileWriter.write(vocabulary.getSortedWords().get(wordIndex) + " " + classMapping.get(wordIndex));
            fileWriter.newLine();
        }
    }
}
