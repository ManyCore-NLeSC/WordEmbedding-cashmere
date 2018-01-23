package nl.esciencecenter.wordembedding.network;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.utilities.ReadWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Word2VecNeuralNetworkWorker extends Thread {
    // Members shared RO with other threads
    private Boolean CBOW;
    private Boolean debug;
    private Boolean hierarchicalSoftmax;
    private Boolean usePosition;
    private Integer MAX_EXP;
    private Integer EXP_TABLE_SIZE;
    private Integer updateInterval = 10000;
    private Integer vectorDimensions;
    private Integer windowSize;
    private Integer negativeSamples;
    private Float alpha;
    private Float samplingFactor;
    private float [] hiddenLayer0;
    private float [] hiddenError0;
    // Members shared RW with other threads
    private Integer globalWordCount;
    private Float currentAlpha;
    private final float [] exponentialTable;
    private final float [] inputLayer;
    private final float [] outputLayer;
    private final float [] outputLayerNegativeSamples;
    private final BufferedReader fileReader;
    private final Vocabulary vocabulary;

    public Word2VecNeuralNetworkWorker(Integer globalWordCount, Float currentAlpha, float [] exponentialTable,
                                       float [] inputLayer, float [] outputLayer, float [] outputLayerNegativeSamples,
                                       Vocabulary vocabulary, BufferedReader fileReader) {
        this.globalWordCount = globalWordCount;
        this.currentAlpha = currentAlpha;
        this.exponentialTable = exponentialTable;
        this.inputLayer = inputLayer;
        this.outputLayer = outputLayer;
        this.outputLayerNegativeSamples = outputLayerNegativeSamples;
        this.vocabulary = vocabulary;
        this.fileReader = fileReader;
    }

    public void setCBOW(Boolean CBOW) {
        this.CBOW = CBOW;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public void setHierarchicalSoftmax(Boolean hierarchicalSoftmax) {
        this.hierarchicalSoftmax = hierarchicalSoftmax;
    }

    public void setUsePosition(Boolean usePosition) {
        this.usePosition = usePosition;
    }

    public void setMAX_EXP(Integer MAX_EXP) {
        this.MAX_EXP = MAX_EXP;
    }

    public void setEXP_TABLE_SIZE(Integer EXP_TABLE_SIZE) {
        this.EXP_TABLE_SIZE = EXP_TABLE_SIZE;
    }

    public void setUpdateInterval(Integer updateInterval) {
        this.updateInterval = updateInterval;
    }

    public void setVectorDimensions(Integer vectorDimensions) {
        this.vectorDimensions = vectorDimensions;
    }

    public void setWindowSize(Integer windowSize) {
        this.windowSize = windowSize;
    }

    public void setNegativeSamples(Integer negativeSamples) {
        this.negativeSamples = negativeSamples;
    }

    public void setAlpha(Float alpha) {
        this.alpha = alpha;
    }

    public void setSamplingFactor(Float samplingFactor) {
        this.samplingFactor = samplingFactor;
    }

    // The code of this method is a straightforward translation of Google's C code
    // TODO: check if it could be written in a better way
    public void run() {
        Integer currentWordCount = 0;
        Integer previousWordCount = 0;
        Integer sentencePosition = 0;
        String line;
        Random randomNumberGenerator = new Random();
        hiddenLayer0 = new float [vectorDimensions];
        hiddenError0 = new float [vectorDimensions];

        // Training loop
        try {
            while ( (line = fileReader.readLine()) != null ) {
                ArrayList<String> sentence = new ArrayList<>();

                if ( (currentWordCount - previousWordCount) > updateInterval ) {
                    synchronized ( globalWordCount ) {
                        globalWordCount += currentWordCount - previousWordCount;
                    }
                    previousWordCount = currentWordCount;
                    if ( debug ) {
                        System.out.format("Alpha: %.10f\t\tProgress: %.2f%%%n", currentAlpha,
                                (globalWordCount / (float)(vocabulary.getOccurrences() + 1)) * 100);
                    }
                    synchronized ( currentAlpha ) {
                        currentAlpha = alpha * (1 - (globalWordCount / (float)(vocabulary.getOccurrences() + 1)));
                        if ( currentAlpha < alpha * 0.0001f ) {
                            currentAlpha = alpha * 0.0001f;
                        }
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
        } catch ( IOException err ) {
            err.printStackTrace();
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
                    synchronized ( outputLayer ) {
                        outputLayer[relatedWordIndex + neuronIndex] = outputLayer[relatedWordIndex + neuronIndex]
                                + (gradient * hiddenLayer0[neuronIndex]);
                    }
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
                    synchronized ( outputLayerNegativeSamples ) {
                        outputLayerNegativeSamples[relatedWordIndex + neuronIndex] =
                                outputLayerNegativeSamples[relatedWordIndex + neuronIndex]
                                        + (gradient * hiddenLayer0[neuronIndex]);
                    }
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
                    synchronized ( inputLayer ) {
                        inputLayer[(lastWordIndex * vectorDimensions) + neuronIndex] =
                                inputLayer[(lastWordIndex * vectorDimensions) + neuronIndex]
                                        + hiddenError0[neuronIndex];
                    }
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
                            synchronized ( outputLayer ) {
                                outputLayer[relatedWordIndexTwo + neuronIndex] =
                                        outputLayer[relatedWordIndexTwo + neuronIndex]
                                                + (gradient * inputLayer[relatedWordIndexOne + neuronIndex]);
                            }
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
                            synchronized ( outputLayerNegativeSamples ) {
                                outputLayerNegativeSamples[relatedWordIndexTwo + neuronIndex] =
                                        outputLayerNegativeSamples[relatedWordIndexTwo + neuronIndex]
                                                + (gradient * inputLayer[relatedWordIndexOne + neuronIndex]);
                            }
                        }
                    }
                }
                for ( int neuronIndex = 0; neuronIndex < vectorDimensions; neuronIndex++ ) {
                    synchronized ( inputLayer ) {
                        inputLayer[relatedWordIndexOne + neuronIndex] =
                                inputLayer[relatedWordIndexOne + neuronIndex] + hiddenError0[neuronIndex];
                    }
                }
            }
        }
    }

}
