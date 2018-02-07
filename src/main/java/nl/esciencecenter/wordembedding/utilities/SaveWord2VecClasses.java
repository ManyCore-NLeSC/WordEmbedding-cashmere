package nl.esciencecenter.wordembedding.utilities;

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
        ArrayList<Float> classVector = new ArrayList<>(nrClasses * neuralNetwork.getVectorDimensions());

        for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
            classMapping.add(wordIndex % nrClasses);
        }
        for ( int iteration = 0; iteration < nrIterations; iteration++ ) {
            for ( int neuronIndex = 0; neuronIndex < nrClasses * neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                classVector.add(0.0f);
            }
            for ( int classIndex = 0; classIndex < nrClasses; classIndex++ ) {
                classCounter.add(1);
            }
            for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    classVector.set(
                            (classMapping.get(wordIndex) * neuralNetwork.getVectorDimensions()) + neuronIndex,
                            classVector.get(
                                    (classMapping.get(wordIndex) * neuralNetwork.getVectorDimensions()) + neuronIndex
                            )
                            + neuralNetwork.getValueInputLayer(
                                    (wordIndex * neuralNetwork.getVectorDimensions()) + neuronIndex
                            )
                    );
                }
                classCounter.set(classMapping.get(wordIndex), classCounter.get(classMapping.get(wordIndex)) + 1);
            }
            for ( int classIndex = 0; classIndex < nrClasses; classIndex++ ) {
                Float distance = 0.0f;

                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    classVector.set((classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex,
                            classVector.get((classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex)
                                    / classCounter.get(classIndex));
                    distance += classVector.get((classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex)
                            * classVector.get((classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex);
                }
                distance = (float)(Math.sqrt(distance));
                for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                    classVector.set(
                            (classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex,
                            classVector.get(
                                    (classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex
                            ) / distance
                    );
                }
            }
            for ( int wordIndex = 0; wordIndex < vocabulary.getNrWords(); wordIndex++ ) {
                Integer classID = 0;
                Float distance = -10.0f;

                for ( int classIndex = 0; classIndex < nrClasses; classIndex++ ) {
                    Float position = 0.0f;

                    for ( int neuronIndex = 0; neuronIndex < neuralNetwork.getVectorDimensions(); neuronIndex++ ) {
                        position += classVector.get((classIndex * neuralNetwork.getVectorDimensions()) + neuronIndex)
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
