package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.data.Word2VecNeuralNetwork;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SaveWord2VecVector {
    // Avoid instantiating the object
    private SaveWord2VecVector() {}

    protected static void save(Vocabulary vocabulary, Word2VecNeuralNetwork neuralNetwork, float [] layer,
                               BufferedWriter fileWriter) throws IOException {
        DecimalFormat sixDecimalFloat = new DecimalFormat("0.000000");

        sixDecimalFloat.setRoundingMode(RoundingMode.CEILING);
        fileWriter.write(vocabulary.getNrWords() + " " + neuralNetwork.getVectorDimensions());
        fileWriter.newLine();
        for ( Word word : vocabulary.getWords() ) {
            fileWriter.write(word.getWord() + " ");
            for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                fileWriter.write(sixDecimalFloat.format(
                        layer[(word.getSortedIndex() * neuralNetwork.getVectorDimensions()) + neuronIndex])
                        + " ");
            }
            fileWriter.newLine();
        }
    }
}
