package nl.esciencecenter.wordembedding.utilities.io;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.NeuralNetworkWord2Vec;

import java.io.BufferedWriter;
import java.io.IOException;

public class SaveWord2VecContextVectors {
    public static void save(Vocabulary vocabulary, NeuralNetworkWord2Vec neuralNetwork, BufferedWriter fileWriter)
            throws IOException {
        SaveWord2VecVectors.save(vocabulary, neuralNetwork, neuralNetwork.getOutputLayerNegativeSamples(), fileWriter);
    }
}
