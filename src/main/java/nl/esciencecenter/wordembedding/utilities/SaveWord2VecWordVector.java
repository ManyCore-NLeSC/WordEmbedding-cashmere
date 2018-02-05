package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word2VecNeuralNetwork;

import java.io.BufferedWriter;
import java.io.IOException;

public class SaveWord2VecWordVector extends SaveWord2VecVector {
    public static void save(Vocabulary vocabulary, Word2VecNeuralNetwork neuralNetwork, BufferedWriter fileWriter)
            throws IOException {
        SaveWord2VecVector.save(vocabulary, neuralNetwork, neuralNetwork.getInputLayer(), fileWriter);
    }
}
