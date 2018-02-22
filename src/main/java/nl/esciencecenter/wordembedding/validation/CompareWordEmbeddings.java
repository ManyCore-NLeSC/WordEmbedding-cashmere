package nl.esciencecenter.wordembedding.validation;

import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.math.FloatComparison;
import nl.esciencecenter.wordembedding.utilities.NearestNeighborsWordEmbedding;

import java.util.HashMap;
import java.util.HashSet;

public class CompareWordEmbeddings {
    public static Boolean compareDimensionality(WordEmbedding [] embeddings) {
        for ( int embeddingID = 1; embeddingID < embeddings.length; embeddingID++ ) {
            if ( !embeddings[embeddingID].getVectorDimensions().equals(embeddings[0].getVectorDimensions()) ) {
                return false;
            }
        }
        return true;
    }

    public static Boolean compareNumericalIdentity(WordEmbedding [] embeddings) {
        return compareNumericalIdentity(embeddings, false);
    }

    public static Boolean compareNumericalIdentity(WordEmbedding [] embeddings, Boolean allowError) {
        if ( embeddings.length > 1 ) {
            for ( int embeddingID = 1; embeddingID < embeddings.length; embeddingID++ ) {
                for ( String referenceWord : embeddings[0].getWords() ) {
                    Float [] referenceCoordinates = embeddings[0].getWordCoordinates(referenceWord);
                    Float [] coordinates = embeddings[embeddingID].getWordCoordinates(referenceWord);
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

    public static Float compareNearestNeighbors(WordEmbedding [] embeddings, Integer percentage) {
        Integer nrSkippedWords = 0;
        Float averageNeighborhoodIntersection = 0.0f;
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
