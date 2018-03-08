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
    private final int updateInterval = 10000;
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
        long currentWordCount = 0;
        long previousWordCount = 0;
        int sentencePosition = 0;
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
                        printUpdateInfo((int)(Thread.currentThread().getId()), neuralNetwork.getCurrentAlpha(),
                                (neuralNetwork.getGlobalWordCount()
                                        / (float)(vocabulary.getOccurrences() + 1)) * 100);
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
                            float sample = (float)((Math.sqrt(vocabulary.getWord(word).getOccurrences()
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
                // Increment the word counter for every sentence to account for "</s>"
                currentWordCount++;
                if ( sentence.size() == 0 ) {
                    // If there are no words in the sentence, read another line.
                    continue;
                }
                String word = sentence.get(sentencePosition);
                for ( int neuronIndex = 0; neuronIndex < hiddenLayer0.length; neuronIndex++ ) {
                    hiddenLayer0[neuronIndex] = 0.0f;
                    hiddenError0[neuronIndex] = 0.0f;
                }
                int randomStartingWord = randomNumberGenerator.nextInt() % neuralNetwork.getWindowSize();
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
        neuralNetwork.incrementGlobalWordCount(currentWordCount - previousWordCount);
        if ( debug ) {
            printUpdateInfo((int)(Thread.currentThread().getId()), neuralNetwork.getCurrentAlpha(),
                    (neuralNetwork.getGlobalWordCount()
                            / (float)(vocabulary.getOccurrences() + 1)) * 100);
        }
        neuralNetwork.setCurrentAlpha(neuralNetwork.getAlpha()
                * (1 - (neuralNetwork.getGlobalWordCount() / (float)(vocabulary.getOccurrences() + 1))));
        if ( neuralNetwork.getCurrentAlpha() < neuralNetwork.getAlpha() * 0.0001f ) {
            neuralNetwork.setCurrentAlpha(neuralNetwork.getAlpha() * 0.0001f);
        }
    }

    // The code of this method is a straightforward translation of Google's C code
    // TODO: check if it could be written in a better way
    private void CBOW(Vocabulary vocabulary, ArrayList<String> sentence, String word, int sentencePosition,
                      int randomStartingWord) {
        for ( int wordIndex = randomStartingWord;
              wordIndex < (neuralNetwork.getWindowSize() * 2 + 1) - randomStartingWord;
              wordIndex++ ) {
            if ( wordIndex != neuralNetwork.getWindowSize() ) {
                int lastWordIndex = sentencePosition - neuralNetwork.getWindowSize() + wordIndex;
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
                float exponential = 0.0f;
                float gradient;
                int relatedWordIndex = vocabulary.getWord(word).getPoint(symbolIndex)
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
            int target;
            int label;
            int relatedWordIndex;
            float exponential;
            float gradient;
            Random randomNumberGenerator = new Random();

            for ( int sample = 0; sample < neuralNetwork.getNegativeSamples() + 1; sample++ ) {
                if ( sample == 0 ) {
                    target = vocabulary.getWord(word).getSortedIndex();
                    label = 1;
                } else {
                    target = randomNumberGenerator.nextInt(vocabulary.getNrWords());
                    if ( target == 0 ) {
                        target = randomNumberGenerator.nextInt(vocabulary.getNrWords()) + 1;
                    } else if ( target == vocabulary.getWord(word).getSortedIndex() ) {
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
                int lastWordIndex = sentencePosition - neuralNetwork.getWindowSize() + wordIndex;

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
    private void skipGram(Vocabulary vocabulary, ArrayList<String> sentence, String word, int sentencePosition,
                          int randomStartingWord) {
        int lastWordIndex;
        int relatedWordIndexOne;
        int relatedWordIndexTwo;
        float exponential;
        float gradient;

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
                    int target;
                    int label;
                    Random randomNumberGenerator = new Random();

                    for ( int sample = 0; sample < neuralNetwork.getNegativeSamples() + 1; sample++ ) {
                        if ( sample == 0 ) {
                            target = vocabulary.getWord(word).getSortedIndex();
                            label = 1;
                        } else {
                            target = randomNumberGenerator.nextInt(vocabulary.getNrWords());
                            if ( target == 0 ) {
                                target = randomNumberGenerator.nextInt(vocabulary.getNrWords()) + 1;
                            } else if ( target == vocabulary.getWord(word).getSortedIndex() ) {
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

    private void printUpdateInfo(int threadId, float currentAlpha, float progress) {
        System.out.format("Thread: %d\t\tAlpha: %.10f\t\tProgress: %.2f%%%n", threadId, currentAlpha, progress);
    }

    private float computeGradient(float exponential, int label) {
        float gradient;
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
