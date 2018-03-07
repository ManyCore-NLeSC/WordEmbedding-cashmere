package nl.esciencecenter.wordembedding.utilities.io;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.data.NeuralNetworkWord2Vec;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

class SaveWord2VecVectors {
    static void save(Vocabulary vocabulary, NeuralNetworkWord2Vec neuralNetwork, float[] layer,
                     BufferedWriter fileWriter) throws IOException {
        DecimalFormat sixDecimalfloat = new DecimalFormat("0.000000");

        sixDecimalfloat.setRoundingMode(RoundingMode.CEILING);
        fileWriter.write(vocabulary.getNrWords() + " " + neuralNetwork.getVectorDimensions());
        fileWriter.newLine();
        for ( Word word : vocabulary.getWords() ) {
            fileWriter.write(word.getWord() + " ");
            for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                fileWriter.write(sixDecimalfloat.format(
                        layer[(word.getSortedIndex() * neuralNetwork.getVectorDimensions()) + neuronIndex])
                        + " ");
            }
            fileWriter.newLine();
        }
    }
}
