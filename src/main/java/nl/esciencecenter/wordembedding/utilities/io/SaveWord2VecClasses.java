package nl.esciencecenter.wordembedding.utilities.io;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.data.NeuralNetworkWord2Vec;

import java.io.BufferedWriter;
import java.io.IOException;

public class SaveWord2VecClasses {
    public static void save(Vocabulary vocabulary, NeuralNetworkWord2Vec neuralNetwork, BufferedWriter fileWriter,
                            int nrClasses) throws IOException {
        int nrIterations = 10;
        int [] classMapping = new  int [vocabulary.getNrWords()];
        int [] classCounter = new int [nrClasses];
        float [] classVector = new float [nrClasses * neuralNetwork.getVectorDimensions()];

        for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
            classMapping[wordIndex] = wordIndex % nrClasses;
        }
        for ( int iteration = 0; iteration < nrIterations; iteration++ ) {
            for ( int neuronIndex = 0; neuronIndex < nrClasses * neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                classVector[neuronIndex] = 0.0f;
            }
            for ( int classIndex = 0; classIndex < nrClasses; classIndex++ ) {
                classCounter[classIndex] = 1;
            }
            for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    classVector[(classMapping[wordIndex] * neuralNetwork.getVectorDimensions()) + neuronIndex] +=
                            neuralNetwork.getValueInputLayer(
                                    (wordIndex * neuralNetwork.getVectorDimensions()) + neuronIndex
                            );
                }
                classCounter[classMapping[wordIndex]] += 1;
            }
            for ( int classIndex = 0; classIndex < nrClasses; classIndex++ ) {
                float distance = 0.0f;

                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    classVector[(classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex] /=
                            classCounter[classIndex];
                    distance += classVector[(classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex]
                            * classVector[(classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex];
                }
                distance = (float)(Math.sqrt(distance));
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    classVector[(classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex] /= distance;
                }
            }
            for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
                int classID = 0;
                float distance = -10.0f;

                for ( int classIndex = 0; classIndex < nrClasses; classIndex++ ) {
                    float position = 0.0f;

                    for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                        position += classVector[(classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex]
                                * neuralNetwork.getValueInputLayer(
                                (wordIndex * neuralNetwork.getVectorDimensions()) + neuronIndex
                        );
                    }
                    if ( position > distance ) {
                        distance = position;
                        classID = classIndex;
                    }
                }
                classMapping[wordIndex] = classID;
            }
        }
        for ( Word word : vocabulary.getWords() ) {
            fileWriter.write( word.getWord() + " " + classMapping[word.getSortedIndex()]);
            fileWriter.newLine();
        }
    }
}
