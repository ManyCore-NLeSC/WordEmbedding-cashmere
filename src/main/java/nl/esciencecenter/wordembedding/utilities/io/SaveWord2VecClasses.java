package nl.esciencecenter.wordembedding.utilities.io;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;
import nl.esciencecenter.wordembedding.data.NeuralNetworkWord2Vec;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SaveWord2VecClasses {
    public static void save(Vocabulary vocabulary, NeuralNetworkWord2Vec neuralNetwork, BufferedWriter fileWriter,
                            Integer nrClasses) throws IOException {
        Integer nrIterations = 10;
        ArrayList<Integer> classMapping = new ArrayList<>(vocabulary.getNrWords());
        ArrayList<Integer> classCounter = new ArrayList<>(nrClasses);
        float [] classVector = new float [nrClasses * neuralNetwork.getVectorDimensions()];

        for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
            classMapping.add(wordIndex % nrClasses);
        }
        for ( int iteration = 0; iteration < nrIterations; iteration++ ) {
            for ( int neuronIndex = 0; neuronIndex < nrClasses * neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                classVector[neuronIndex] = 0.0f;
            }
            for ( int classIndex = 0; classIndex < nrClasses; classIndex++ ) {
                classCounter.add(1);
            }
            for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    classVector[(classMapping.get(wordIndex) * neuralNetwork.getVectorDimensions()) + neuronIndex] +=
                            neuralNetwork.getValueInputLayer(
                                    (wordIndex * neuralNetwork.getVectorDimensions()) + neuronIndex
                            );
                }
                classCounter.set(classMapping.get(wordIndex), classCounter.get(classMapping.get(wordIndex)) + 1);
            }
            for ( int classIndex = 0; classIndex < nrClasses; classIndex++ ) {
                float distance = 0.0f;

                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    classVector[(classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex] /=
                            classCounter.get(classIndex);
                    distance += classVector[(classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex]
                            * classVector[(classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex];
                }
                distance = (float)(Math.sqrt(distance));
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    classVector[(classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex] /= distance;
                }
            }
            for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
                Integer classID = 0;
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
                classMapping.set(wordIndex, classID);
            }
        }
        for ( Word word : vocabulary.getWords() ) {
            fileWriter.write( word.getWord() + " " + classMapping.get(word.getSortedIndex()));
            fileWriter.newLine();
        }
    }
}
