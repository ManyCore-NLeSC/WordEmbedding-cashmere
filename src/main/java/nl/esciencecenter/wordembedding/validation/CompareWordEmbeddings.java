package nl.esciencecenter.wordembedding.validation;

import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.math.FloatComparison;
import nl.esciencecenter.wordembedding.utilities.NearestNeighborsWordEmbedding;

import java.util.HashMap;
import java.util.HashSet;

public class CompareWordEmbeddings {
    public static boolean compareDimensionality(WordEmbedding [] embeddings) {
        for ( int embeddingID = 1; embeddingID < embeddings.length; embeddingID++ ) {
            if ( embeddings[embeddingID].getVectorDimensions() != embeddings[0].getVectorDimensions() ) {
                return false;
            }
        }
        return true;
    }

    public static boolean compareNumericalIdentity(WordEmbedding [] embeddings) {
        return compareNumericalIdentity(embeddings, false);
    }

    public static boolean compareNumericalIdentity(WordEmbedding [] embeddings, boolean allowError) {
        if ( embeddings.length > 1 ) {
            for ( int embeddingID = 1; embeddingID < embeddings.length; embeddingID++ ) {
                for ( String referenceWord : embeddings[0].getWords() ) {
                    float [] referenceCoordinates = embeddings[0].getWordCoordinates(referenceWord);
                    float [] coordinates = embeddings[embeddingID].getWordCoordinates(referenceWord);
                    if ( coordinates == null ) {
                        return false;
                    }
                    for ( int dimension = 0; dimension < embeddings[0].getVectorDimensions(); dimension++ ) {
                        if ( allowError ) {
                            if ( !FloatComparison.areSimilar(referenceCoordinates[dimension],
                                    coordinates[dimension], 1.0e-06f) ) {
                                return false;
                            }
                        } else {
                            if ( !FloatComparison.areIdentical(referenceCoordinates[dimension],
                                    coordinates[dimension]) ) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static float compareNearestNeighbors(WordEmbedding [] embeddings, int percentage) {
        int nrSkippedWords = 0;
        float averageNeighborhoodIntersection = 0.0f;
        HashMap<String, HashMap<Integer, String []>> neighbors
                = NearestNeighborsWordEmbedding.compute(embeddings);

        for ( String referenceWord : embeddings[0].getWords() ) {
            HashSet<String> neighborhoodIntersection = new HashSet<>();

            if ( !neighbors.containsKey(referenceWord) ) {
                nrSkippedWords++;
                continue;
            }
            for ( int embedding = 0; embedding < embeddings.length; embedding++ ) {
                HashSet<String> neighborhood = new HashSet<>();

                for ( int neighborID = 0;
                      neighborID < ((embeddings[0].getNrWords() - 1) * percentage) / 100;
                      neighborID++ ) {
                    neighborhood.add(neighbors.get(referenceWord).get(embedding)[neighborID]);
                }
                if ( embedding == 0 ) {
                    neighborhoodIntersection.addAll(neighborhood);
                } else {
                    neighborhoodIntersection.retainAll(neighborhood);
                }
            }
            averageNeighborhoodIntersection += neighborhoodIntersection.size();
        }
        return averageNeighborhoodIntersection / (embeddings[0].getNrWords() - nrSkippedWords);
    }
}
