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
    private boolean progress;
    private boolean threadSynchronization;
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

    public void setProgress(boolean progress) {
        this.progress = progress;
    }

    public void setThreadSynchronization(boolean synchronization)
    {
        this.threadSynchronization = synchronization;
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
            ArrayList<String> sentence = new ArrayList<>();
	        // loops over the whole file, processed one line at the time
            while ( (line = fileReader.readLine()) != null ) {
                // Updating learning rate and print update message
                if ( (currentWordCount - previousWordCount) > updateInterval ) {
                    synchronized ( neuralNetwork ) {
                        neuralNetwork.incrementGlobalWordCount(currentWordCount - previousWordCount);
                    }
                    previousWordCount = currentWordCount;
                    if (progress) {
                        printUpdateInfo((int)(Thread.currentThread().getId()), neuralNetwork.getCurrentAlpha(),
                                (neuralNetwork.getGlobalWordCount()
                                        / (float)((neuralNetwork.getNrIterations() * vocabulary.getOccurrences()) + 1)) * 100);
                    }
                    synchronized ( neuralNetwork ) {
                        neuralNetwork.setCurrentAlpha(neuralNetwork.getAlpha()
                            * (1 - (neuralNetwork.getGlobalWordCount() / (float)(vocabulary.getOccurrences() + 1))));
                        if ( neuralNetwork.getCurrentAlpha() < neuralNetwork.getAlpha() * 0.0001f ) {
                            neuralNetwork.setCurrentAlpha(neuralNetwork.getAlpha() * 0.0001f);
                        }
                    }
                }
                // Create the sentence by removing all words that are not in the vocabulary
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
                // Increment the word counter for every sentence to account for "</s>"
                currentWordCount++;
                // Process the sentence
                while ( sentence.size() != 0 )
                {
                    String word = sentence.get(sentencePosition);
                    for ( int neuronIndex = 0; neuronIndex < hiddenLayer0.length; neuronIndex++ ) {
                        hiddenLayer0[neuronIndex] = 0.0f;
                        hiddenError0[neuronIndex] = 0.0f;
                    }
                    int randomStartingWord = randomNumberGenerator.nextInt(neuralNetwork.getWindowSize());
                    if ( neuralNetwork.getCBOW() ) {
                        CBOW(vocabulary, sentence, word, sentencePosition, randomStartingWord);
                    } else {
                        skipGram(vocabulary, sentence, word, sentencePosition, randomStartingWord);
                    }
                    // this is where we move to the next word in the sentence
                    sentencePosition++;
                    if ( sentencePosition >= sentence.size() ) {
                        sentence.clear();
                    }
                }
            }
        } catch ( IOException err ) {
            err.printStackTrace();
        }
        synchronized ( neuralNetwork ) {
            neuralNetwork.incrementGlobalWordCount(currentWordCount - previousWordCount);
        }
        if (progress) {
            printUpdateInfo((int)(Thread.currentThread().getId()), neuralNetwork.getCurrentAlpha(),
                    (neuralNetwork.getGlobalWordCount()
                            / (float)((neuralNetwork.getNrIterations() * vocabulary.getOccurrences()) + 1)) * 100);
            synchronized ( System.out )
            {
                System.out.println();
            }
        }
        synchronized ( neuralNetwork ) {
            neuralNetwork.setCurrentAlpha(neuralNetwork.getAlpha()
                * (1 - (neuralNetwork.getGlobalWordCount() / (float)(vocabulary.getOccurrences() + 1))));
            if ( neuralNetwork.getCurrentAlpha() < neuralNetwork.getAlpha() * 0.0001f ) {
                neuralNetwork.setCurrentAlpha(neuralNetwork.getAlpha() * 0.0001f);
            }
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
                            + neuralNetwork.getValueWordVector(
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
                            * neuralNetwork.getValueHierarchicalSoftMaxLayer(relatedWordIndex + neuronIndex);
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
                            + (gradient * neuralNetwork.getValueHierarchicalSoftMaxLayer(relatedWordIndex + neuronIndex));
                    if ( threadSynchronization )
                    {
                        synchronized ( neuralNetwork )
                        {
                            neuralNetwork.incrementValueHierarchicalSoftMaxLayer(relatedWordIndex + neuronIndex,gradient * hiddenLayer0[neuronIndex]);
                        }
                    }
                    else
                    {
                        neuralNetwork.incrementValueHierarchicalSoftMaxLayer(relatedWordIndex + neuronIndex,gradient * hiddenLayer0[neuronIndex]);
                    }
                }
            }
        }
        if ( neuralNetwork.getNegativeSamples() > 0 ) {
            int relatedWordIndex;
            float exponential;
            float gradient;
            Random randomNumberGenerator = new Random();
            TargetLabel targetLabel =  new TargetLabel(randomNumberGenerator);

            for ( int sample = 0; sample < neuralNetwork.getNegativeSamples() + 1; sample++ ) {
                if ( !targetLabel.compute(sample, word) ) {
                    continue;
                }
                exponential = 0.0f;
                relatedWordIndex = targetLabel.getTarget() * neuralNetwork.getVectorDimensions();
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    exponential += hiddenLayer0[neuronIndex]
                            * neuralNetwork.getValueContextVector(relatedWordIndex + neuronIndex);
                }
                gradient = computeGradient(exponential, targetLabel.getLabel());
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    hiddenError0[neuronIndex] = hiddenError0[neuronIndex]
                            + (gradient
                            * neuralNetwork.getValueContextVector(relatedWordIndex + neuronIndex));
                    if ( threadSynchronization )
                    {
                        synchronized ( neuralNetwork )
                        {
                            neuralNetwork.incrementValueContextVector(relatedWordIndex + neuronIndex, gradient * hiddenLayer0[neuronIndex]);
                        }
                    }
                    else
                    {
                        neuralNetwork.incrementValueContextVector(relatedWordIndex + neuronIndex, gradient * hiddenLayer0[neuronIndex]);
                    }
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
                    if ( threadSynchronization )
                    {
                        synchronized ( neuralNetwork )
                        {
                            neuralNetwork.incrementValueWordVector((lastWordIndex * neuralNetwork.getVectorDimensions()) + neuronIndex, hiddenError0[neuronIndex]);
                        }
                    }
                    else
                    {
                        neuralNetwork.incrementValueWordVector((lastWordIndex * neuralNetwork.getVectorDimensions()) + neuronIndex, hiddenError0[neuronIndex]);
                    }
                }
            }
        }
    }

    // The code of this method is a straightforward translation of Google's C code
    // TODO: check if it could be written in a better way
    // word is the word we are checking, so w, it has position sentencePosition in the sentence.  For some reason
    // we use a randomStartingWord, why?
    private void skipGram(Vocabulary vocabulary, ArrayList<String> sentence, String word, int sentencePosition,
                          int randomStartingWord) {
        int lastWordIndex;
        int relatedWordIndexOne;
        int relatedWordIndexTwo;
        float exponential;
        float gradient;

        for ( int wordIndex = randomStartingWord; wordIndex < (neuralNetwork.getWindowSize() * 2) + 1 - randomStartingWord; wordIndex++ ) {
            if ( wordIndex != neuralNetwork.getWindowSize() ) {
                lastWordIndex = sentencePosition - neuralNetwork.getWindowSize() + wordIndex;
                if ( lastWordIndex < 0 || lastWordIndex >= sentence.size() ) {
                    continue;
                }
                lastWordIndex = vocabulary.getWord(sentence.get(lastWordIndex)).getSortedIndex();
                if ( lastWordIndex == -1 ) {
                    continue;
                }
                // two cases
                //   - let's assume that this is w
                //   - let's assume that this is c (we are no choosing this one)
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
                            exponential += neuralNetwork.getValueWordVector(relatedWordIndexOne + neuronIndex)
                                    * neuralNetwork.getValueHierarchicalSoftMaxLayer(relatedWordIndexTwo + neuronIndex);
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
                                    * neuralNetwork.getValueHierarchicalSoftMaxLayer(relatedWordIndexTwo + neuronIndex));
                            if ( threadSynchronization )
                            {
                                synchronized ( neuralNetwork )
                                {
                                    neuralNetwork.incrementValueHierarchicalSoftMaxLayer(relatedWordIndexTwo + neuronIndex, gradient * neuralNetwork.getValueWordVector(relatedWordIndexOne + neuronIndex));
                                }
                            }
                            else
                            {
                                neuralNetwork.incrementValueHierarchicalSoftMaxLayer(relatedWordIndexTwo + neuronIndex, gradient * neuralNetwork.getValueWordVector(relatedWordIndexOne + neuronIndex));
                            }
                        }
                    }
                }
                if ( neuralNetwork.getNegativeSamples() > 0 ) {
                    Random randomNumberGenerator = new Random();
                    TargetLabel targetLabel = new TargetLabel(randomNumberGenerator);

                    // neuralNetwork.getNegativeSamples() = k
                    // sample represents c_N
                    for ( int sample = 0; sample < neuralNetwork.getNegativeSamples() + 1; sample++ ) {
                        // sample == 0, represents w
                        // if we want to update w, then we should not continue
                        if ( !targetLabel.compute(sample, word) ) {
			                // only if coincedently we draw our own word
                            continue;
                        }
                        if ( neuralNetwork.getUsePosition() ) {
                            relatedWordIndexTwo = (neuralNetwork.getWindowSize() * 2 * targetLabel.getTarget())
                                    * neuralNetwork.getVectorDimensions();
                            if ( wordIndex > neuralNetwork.getWindowSize() ) {
                                relatedWordIndexTwo += wordIndex - 1;
                            } else {
                                relatedWordIndexTwo += wordIndex;
                            }
                        } else {
			    // this is then c_N
			    relatedWordIndexTwo = targetLabel.getTarget() * neuralNetwork.getVectorDimensions();
			}
                        exponential = 0.0f;
                        for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
			                // this is the dotproduct
                            exponential += neuralNetwork.getValueWordVector(relatedWordIndexOne + neuronIndex)
                                    * neuralNetwork.getValueContextVector(relatedWordIndexTwo
                                    + neuronIndex);
                        }
                        // compute the gradient
                        // gives a direction based on label which depends on being sampmle represent w or c_N
                        gradient = computeGradient(exponential, targetLabel.getLabel());
                        for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                            // previous values + the context
                            // this sums over c_N multiplied with the gradient
                            // it is an array of size d (= neuralNetwork.getVectorDimensions())
                            hiddenError0[neuronIndex] = hiddenError0[neuronIndex] + (gradient
                                    * neuralNetwork.getValueContextVector(relatedWordIndexTwo
                                    + neuronIndex));
                            if ( threadSynchronization )
                            {
                                synchronized ( neuralNetwork )
                                {
                                    neuralNetwork.incrementValueContextVector(relatedWordIndexTwo + neuronIndex, gradient * neuralNetwork.getValueWordVector(relatedWordIndexOne + neuronIndex));
                                }
                            }
                            else
                            {
				                // update c_N with w (not w)
                                neuralNetwork.incrementValueContextVector(relatedWordIndexTwo + neuronIndex, gradient * neuralNetwork.getValueWordVector(relatedWordIndexOne + neuronIndex));
                            }
                        }
                    } // end sample loop
	  
		    
                }
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    if ( threadSynchronization )
                    {
                        synchronized ( neuralNetwork )
                        {
                            neuralNetwork.incrementValueWordVector(relatedWordIndexOne + neuronIndex, hiddenError0[neuronIndex]);
                        }
                    }
                    else
                    {
			// update w with hiddenError that represents sum of all combinations of c_N
                        neuralNetwork.incrementValueWordVector(relatedWordIndexOne + neuronIndex, hiddenError0[neuronIndex]);
                    }
                }
            }
        }
    }

    private void printUpdateInfo(int threadId, float currentAlpha, float progress) {
        synchronized ( System.out ) {
            System.out.format("\rThread: %d\t\tAlpha: %.6f\t\tProgress: %.2f%%", threadId, currentAlpha, progress);
            System.out.flush();
        }
    }

    private float computeGradient(float exponential, int label) {
        float gradient;
        if ( exponential > exponentialTable.getMaximumExponential() ) {
            gradient = (label - 1) * neuralNetwork.getCurrentAlpha();
        } else if ( exponential < -exponentialTable.getMaximumExponential() ) {
            gradient = label * neuralNetwork.getCurrentAlpha();
        } else {
	    // probably the most used case
            gradient = (label // only 0 or 1
                    - exponentialTable.get((int)((exponential + exponentialTable.getMaximumExponential())
                    * (exponentialTable.getTableSize() / exponentialTable.getMaximumExponential()
                    / 2)))) * neuralNetwork.getCurrentAlpha();
        }
        return gradient;
    }

    private class TargetLabel {
        private int target;
	// either set to the index of w?

	// either 0 or 1, but what does it mean?
        private int label;
        private final Random randomNumberGenerator;

        private TargetLabel(Random randomNumberGenerator) {
            this.randomNumberGenerator = randomNumberGenerator;
        }

        private int getTarget() {
            return target;
        }

        private int getLabel() {
            return label;
        }

        private boolean compute(int sample, String word) {
            if ( sample == 0 ) {
		// this means we're dealing with sample representing w
		// label has influence on the direction of the gradient (negative or positive)
                target = vocabulary.getWord(word).getSortedIndex();
                label = 1;
            } else {
                target = randomNumberGenerator.nextInt(vocabulary.getNrWords());
                if ((vocabulary.getWord("</s>") != null) && (target == vocabulary.getWord("</s>").getSortedIndex())) {
                    target = randomNumberGenerator.nextInt(vocabulary.getNrWords());
                } else if ( target == vocabulary.getWord(word).getSortedIndex() ) {
                    return false;
                }
                label = 0;
            }
            return true;
        }
    }
}
