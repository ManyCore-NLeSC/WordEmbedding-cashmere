package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.ExponentialTable;
import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.NeuralNetworkWord2Vec;
import nl.esciencecenter.wordembedding.utilities.io.ReadWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TrainWord2VecModel extends Thread {
    private Boolean debug;
    private final Integer updateInterval = 10000;
    private final Vocabulary vocabulary;
    private final NeuralNetworkWord2Vec neuralNetwork;
    private final BufferedReader fileReader;
    private ExponentialTable exponentialTable;
    private float [] hiddenLayer0;
    private float [] hiddenError0;

    public TrainWord2VecModel(Vocabulary vocabulary, NeuralNetworkWord2Vec neuralNetwork, BufferedReader fileReader) {
        this.vocabulary = vocabulary;
        this.neuralNetwork = neuralNetwork;
        this.fileReader = fileReader;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public void setExponentialTable(ExponentialTable exponentialTable) {
        this.exponentialTable = exponentialTable;
    }

    public void run() {
        Integer currentWordCount = 0;
        Integer previousWordCount = 0;
        Integer sentencePosition = 0;
        String line;
        Random randomNumberGenerator = new Random();
        hiddenLayer0 = new float [neuralNetwork.getVectorDimensions()];
        hiddenError0 = new float [neuralNetwork.getVectorDimensions()];

        // Training loop
        try {
            while ( (line = fileReader.readLine()) != null ) {
                ArrayList<String> sentence = new ArrayList<>();

                if ( (currentWordCount - previousWordCount) > updateInterval ) {
                    neuralNetwork.incrementGlobalWordCount(currentWordCount - previousWordCount);
                    previousWordCount = currentWordCount;
                    if ( debug ) {
                        System.out.format("Thread: %d\t\tAlpha: %.10f\t\tProgress: %.2f%%%n",
                                Thread.currentThread().getId(),
                                neuralNetwork.getCurrentAlpha(),
                                (neuralNetwork.getGlobalWordCount() / (float)(vocabulary.getOccurrences() + 1)) * 100);
                    }
                    neuralNetwork.setCurrentAlpha(neuralNetwork.getAlpha()
                            * (1 - (neuralNetwork.getGlobalWordCount() / (float)(vocabulary.getOccurrences() + 1))));
                    if ( neuralNetwork.getCurrentAlpha() < neuralNetwork.getAlpha() * 0.0001f ) {
                        neuralNetwork.setCurrentAlpha(neuralNetwork.getAlpha() * 0.0001f);
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
                        if ( neuralNetwork.getSamplingFactor() > 0 ) {
                            Float sample = (float)((Math.sqrt(vocabulary.getWord(word).getOccurrences()
                                    / (neuralNetwork.getSamplingFactor() * vocabulary.getNrWords())) + 1)
                                    * (neuralNetwork.getSamplingFactor() * vocabulary.getNrWords())
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
                Integer randomStartingWord = randomNumberGenerator.nextInt() % neuralNetwork.getWindowSize();
                if ( neuralNetwork.getCBOW() ) {
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
        for ( int wordIndex = randomStartingWord;
              wordIndex < (neuralNetwork.getWindowSize() * 2 + 1) - randomStartingWord;
              wordIndex++ ) {
            if ( wordIndex != neuralNetwork.getWindowSize() ) {
                Integer lastWordIndex = sentencePosition - neuralNetwork.getWindowSize() + wordIndex;
                if ( lastWordIndex < 0 || lastWordIndex >= sentence.size() ) {
                    continue;
                }
                lastWordIndex = vocabulary.getWord(sentence.get(lastWordIndex)).getSortedIndex();
                if ( lastWordIndex == -1 ) {
                    continue;
                }
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    hiddenLayer0[neuronIndex] = hiddenLayer0[neuronIndex]
                            + neuralNetwork.getValueInputLayer(
                                    (lastWordIndex * neuralNetwork.getVectorDimensions()) + neuronIndex);
                }
            }
        }
        if ( neuralNetwork.getHierarchicalSoftmax() ) {
            for ( int symbolIndex = 0; symbolIndex < vocabulary.getWord(word).getCodeLength(); symbolIndex++ ) {
                Float exponential = 0.0f;
                Float gradient;
                Integer relatedWordIndex = vocabulary.getWord(word).getPoint(symbolIndex)
                        * neuralNetwork.getVectorDimensions();

                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    exponential += hiddenLayer0[neuronIndex]
                            * neuralNetwork.getValueOutputLayer(relatedWordIndex + neuronIndex);
                }
                if ( (exponential <= -exponentialTable.getMaximumExponential())
                        || (exponential >= exponentialTable.getMaximumExponential()) ) {
                    continue;
                }
                exponential = exponentialTable.get((int)((exponential + exponentialTable.getMaximumExponential())
                        * (exponentialTable.getTableSize() / exponentialTable.getMaximumExponential() / 2)));
                gradient = (1 - vocabulary.getWord(word).getCode(symbolIndex) - exponential)
                        * neuralNetwork.getCurrentAlpha();
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    hiddenError0[neuronIndex] = hiddenError0[neuronIndex]
                            + (gradient * neuralNetwork.getValueOutputLayer(relatedWordIndex + neuronIndex));
                    neuralNetwork.incrementValueOutputLayer(relatedWordIndex + neuronIndex,
                            gradient * hiddenLayer0[neuronIndex]);
                }
            }
        }
        if ( neuralNetwork.getNegativeSamples() > 0 ) {
            Integer target;
            Integer label;
            Integer relatedWordIndex;
            Float exponential;
            Float gradient;
            Random randomNumberGenerator = new Random();

            for ( int sample = 0; sample < neuralNetwork.getNegativeSamples() + 1; sample++ ) {
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
                relatedWordIndex = target * neuralNetwork.getVectorDimensions();
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    exponential += hiddenLayer0[neuronIndex]
                            * neuralNetwork.getValueOutputLayerNegativeSamples(relatedWordIndex + neuronIndex);
                }
                gradient = computeGradient(exponential, label);
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    hiddenError0[neuronIndex] = hiddenError0[neuronIndex]
                            + (gradient
                            * neuralNetwork.getValueOutputLayerNegativeSamples(relatedWordIndex + neuronIndex));
                    neuralNetwork.incrementValueOutputNegativeSamples(relatedWordIndex + neuronIndex,
                            gradient * hiddenLayer0[neuronIndex]);
                }
            }
        }
        for ( int wordIndex = randomStartingWord; wordIndex < (neuralNetwork.getWindowSize() * 2) + 1; wordIndex++ ) {
            if ( wordIndex != neuralNetwork.getWindowSize() ) {
                Integer lastWordIndex = sentencePosition - neuralNetwork.getWindowSize() + wordIndex;

                if ( lastWordIndex < 0 || lastWordIndex >= sentence.size() ) {
                    continue;
                }
                lastWordIndex = vocabulary.getWord(sentence.get(lastWordIndex)).getSortedIndex();
                if ( lastWordIndex == -1 ) {
                    continue;
                }
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    neuralNetwork.incrementValueInputLayer((lastWordIndex * neuralNetwork.getVectorDimensions())
                            + neuronIndex,
                            hiddenError0[neuronIndex]);
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

        for ( int wordIndex = randomStartingWord; wordIndex < (neuralNetwork.getWindowSize() * 2) - 1; wordIndex++ ) {
            if ( wordIndex != neuralNetwork.getWindowSize() ) {
                lastWordIndex = sentencePosition - neuralNetwork.getWindowSize() + wordIndex;
                if ( lastWordIndex < 0 || lastWordIndex >= sentence.size() ) {
                    continue;
                }
                lastWordIndex = vocabulary.getWord(sentence.get(lastWordIndex)).getSortedIndex();
                if ( lastWordIndex == -1 ) {
                    continue;
                }
                relatedWordIndexOne = lastWordIndex * neuralNetwork.getVectorDimensions();
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    hiddenError0[neuronIndex] = 0.0f;
                }
                if ( neuralNetwork.getHierarchicalSoftmax() ) {
                    for ( int symbolIndex = 0; symbolIndex < vocabulary.getWord(word).getCodeLength(); symbolIndex++ ) {
                        exponential = 0.0f;
                        relatedWordIndexTwo = vocabulary.getWord(word).getPoint(symbolIndex)
                                * neuralNetwork.getVectorDimensions();
                        for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                            exponential += neuralNetwork.getValueInputLayer(relatedWordIndexOne + neuronIndex)
                                    * neuralNetwork.getValueOutputLayer(relatedWordIndexTwo + neuronIndex);
                        }
                        if ( (exponential <= -exponentialTable.getMaximumExponential())
                                || (exponential >= exponentialTable.getMaximumExponential()) ) {
                            continue;
                        }
                        exponential = exponentialTable.get((int)((exponential
                                + exponentialTable.getMaximumExponential())
                                * (exponentialTable.getTableSize() / exponentialTable.getMaximumExponential() / 2)));
                        gradient = (1 - vocabulary.getWord(word).getCode(symbolIndex) - exponential)
                                * neuralNetwork.getCurrentAlpha();
                        for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                            hiddenError0[neuronIndex] = hiddenError0[neuronIndex]
                                    + (gradient
                                    * neuralNetwork.getValueOutputLayer(relatedWordIndexTwo + neuronIndex));
                            neuralNetwork.incrementValueOutputLayer(relatedWordIndexTwo + neuronIndex,
                                    gradient
                                            * neuralNetwork.getValueInputLayer(relatedWordIndexOne + neuronIndex));
                        }
                    }
                }
                if ( neuralNetwork.getNegativeSamples() > 0 ) {
                    Integer target;
                    Integer label;
                    Random randomNumberGenerator = new Random();

                    for ( int sample = 0; sample < neuralNetwork.getNegativeSamples() + 1; sample++ ) {
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
                        if ( neuralNetwork.getUsePosition() ) {
                            relatedWordIndexTwo = (neuralNetwork.getWindowSize() * 2 * target)
                                    * neuralNetwork.getVectorDimensions();
                            if ( wordIndex > neuralNetwork.getWindowSize() ) {
                                relatedWordIndexTwo += wordIndex - 1;
                            } else {
                                relatedWordIndexTwo += wordIndex;
                            }
                        } else {
                            relatedWordIndexTwo = target * neuralNetwork.getVectorDimensions();
                        }
                        exponential = 0.0f;
                        for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                            exponential += neuralNetwork.getValueInputLayer(relatedWordIndexOne + neuronIndex)
                                    * neuralNetwork.getValueOutputLayerNegativeSamples(relatedWordIndexTwo
                                    + neuronIndex);
                        }
                        gradient = computeGradient(exponential, label);
                        for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                            hiddenError0[neuronIndex] = hiddenError0[neuronIndex] + (gradient
                                    * neuralNetwork.getValueOutputLayerNegativeSamples(relatedWordIndexTwo
                                    + neuronIndex));
                            neuralNetwork.incrementValueOutputNegativeSamples(relatedWordIndexTwo + neuronIndex,
                                    gradient * neuralNetwork.getValueInputLayer(
                                            relatedWordIndexOne + neuronIndex));
                        }
                    }
                }
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    neuralNetwork.incrementValueInputLayer(relatedWordIndexOne + neuronIndex,
                            hiddenError0[neuronIndex]);
                }
            }
        }
    }

    private Float computeGradient(Float exponential, Integer label) {
        Float gradient;
        if ( exponential > exponentialTable.getMaximumExponential() ) {
            gradient = (label - 1) * neuralNetwork.getCurrentAlpha();
        } else if ( exponential < -exponentialTable.getMaximumExponential() ) {
            gradient = label * neuralNetwork.getCurrentAlpha();
        } else {
            gradient = (label
                    - exponentialTable.get((int)((exponential + exponentialTable.getMaximumExponential())
                    * (exponentialTable.getTableSize() / exponentialTable.getMaximumExponential()
                    / 2)))) * neuralNetwork.getCurrentAlpha();
        }
        return gradient;
    }
}
